package org.unidal.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.plexus.logging.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.logger.LoggerFactory;

class MessageSender implements Task {
   private MessageDelegate m_delegate;

   private int m_port;

   private String[] m_servers;

   private int m_maxThreads;

   private String m_threadNamePrefix;

   private ChannelManager m_manager;

   private Logger m_logger = LoggerFactory.getLogger(MessageReceiver.class);

   private boolean m_active;

   private static ConcurrentMap<String, AtomicInteger> m_indexes = new ConcurrentHashMap<String, AtomicInteger>();

   private AtomicInteger m_attempts = new AtomicInteger();

   public MessageSender(MessageDelegate delegate, int port, String... servers) {
      m_delegate = delegate;
      m_port = port;
      m_servers = servers;
   }

   private boolean checkWritable(ChannelFuture future) {
      boolean isWriteable = false;

      if (future != null && future.getChannel().isOpen()) {
         if (future.getChannel().isWritable()) {
            isWriteable = true;
         } else {
            int count = m_attempts.incrementAndGet();

            if (count % 1000 == 0 || count == 1) {
               m_logger.error("Netty write buffer is full! Attempts: " + count + ".");
            }
         }
      }

      return isWriteable;
   }

   @Override
   public String getName() {
      return getClass().getSimpleName();
   }

   private String getUniquePrefix() {
      String name;

      if (m_threadNamePrefix == null) {
         String className = new Exception().getStackTrace()[2].getClassName();
         int pos = className.lastIndexOf('.');

         name = className.substring(pos + 1);
      } else {
         name = m_threadNamePrefix;
      }

      m_indexes.putIfAbsent(name, new AtomicInteger(1));

      AtomicInteger index = m_indexes.get(name);

      if (index.getAndIncrement() > 1) {
         name += index.get();
      }

      return name;
   }

   @Override
   public void run() {
      m_active = true;

      while (m_active) {
         ChannelFuture future = m_manager.getChannel();

         if (checkWritable(future)) {
            try {
               ChannelBuffer buffer = m_delegate.nextMessage(5, TimeUnit.MILLISECONDS);

               if (buffer != null) {
                  Channel channel = future.getChannel();

                  channel.write(buffer);
               }
            } catch (Throwable t) {
               m_logger.error("Error when sending message over TCP socket!", t);
            }
         } else {
            try {
               TimeUnit.MILLISECONDS.sleep(5);
            } catch (Exception e) {
               // ignore it
               m_active = false;
            }
         }
      }

      m_manager.shutdown();
   }

   public void setMaxThreads(int maxThreads) {
      m_maxThreads = maxThreads;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      m_threadNamePrefix = threadNamePrefix;
   }

   @Override
   public void shutdown() {
      m_active = false;
      m_manager.shutdown();
   }

   public void startClient() {
      String name = getUniquePrefix();

      m_active = true;
      m_manager = new ChannelManager();

      Threads.forGroup(name).start(this);
      Threads.forGroup(name).start(m_manager);
   }

   class ChannelManager implements Task {
      private List<InetSocketAddress> m_serverAddresses;

      private ClientBootstrap m_bootstrap;

      private ChannelFuture m_activeFuture;

      private int m_activeIndex = -1;

      private ChannelFuture m_lastFuture;

      private AtomicInteger m_reconnects = new AtomicInteger(999);

      public ChannelManager() {
         NioClientSocketChannelFactory factory;
         String name = getUniquePrefix();

         if (m_maxThreads > 0) {
            ExecutorService bossExecutor = Threads.forPool().getFixedThreadPool(name + "-Boss", 10);
            ExecutorService workerExecutor = Threads.forPool().getFixedThreadPool(name + "-Worker", m_maxThreads);
            factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
         } else {
            ExecutorService bossExecutor = Threads.forPool().getCachedThreadPool(name + "-Boss");
            ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(name + "-Worker");
            factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
         }

         ClientBootstrap bootstrap = new ClientBootstrap(factory);

         bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
               return Channels.pipeline(new ExceptionHandler(m_logger));
            }
         });

         bootstrap.setOption("tcpNoDelay", true);
         bootstrap.setOption("keepAlive", true);
         bootstrap.setOption("connectTimeoutMillis", 2000);

         m_bootstrap = bootstrap;
         m_serverAddresses = new ArrayList<InetSocketAddress>();

         for (String server : m_servers) {
            m_serverAddresses.add(new InetSocketAddress(server, m_port));
         }

         int len = m_servers.length;

         for (int i = 0; i < len; i++) {
            ChannelFuture future = createChannel(i);

            if (future != null) {
               m_activeFuture = future;
               m_activeIndex = i;
               break;
            }
         }
      }

      ChannelFuture createChannel(int index) {
         InetSocketAddress address = m_serverAddresses.get(index);
         ChannelFuture future = m_bootstrap.connect(address);

         future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100ms

         if (!future.isSuccess()) {
            future.getChannel().getCloseFuture().awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100ms
            int count = m_reconnects.incrementAndGet();

            if (count % 1000 == 0) {
               m_logger.error("Error when connecting to " + address + ", message: " + future.getCause() + ", " + count);
            }

            return null;
         } else {
            m_logger.info("Connected to " + address + ".");
            return future;
         }
      }

      public ChannelFuture getChannel() {
         if (m_lastFuture != null && m_lastFuture != m_activeFuture) {
            m_lastFuture.getChannel().close();
            m_lastFuture = null;
         }

         return m_activeFuture;
      }

      @Override
      public String getName() {
         return MessageSender.this.getClass().getSimpleName() + "-" + getClass().getSimpleName();
      }

      @Override
      public void run() {
         try {
            while (m_active) {
               try {
                  if (m_activeIndex == -1 || m_activeFuture != null && !m_activeFuture.getChannel().isOpen()) {
                     m_activeIndex = m_serverAddresses.size();
                  }

                  for (int i = 0; i < m_activeIndex; i++) {
                     ChannelFuture future = createChannel(i);

                     if (future != null) {
                        m_lastFuture = m_activeFuture;
                        m_activeFuture = future;
                        m_activeIndex = i;
                        break;
                     }
                  }
               } catch (Throwable e) {
                  m_logger.error("Error happened in ChannelManager.", e);
               }

               Thread.sleep(2 * 1000L); // check every 2 seconds
            }
         } catch (InterruptedException e) {
            // ignore
         }
      }

      @Override
      public void shutdown() {
         m_active = false;

         if (m_activeFuture != null) {
            m_activeFuture.getChannel().close();
            m_activeFuture = null;
         }

         m_bootstrap.getFactory().releaseExternalResources();
      }
   }

   static class ExceptionHandler extends SimpleChannelHandler {
      private Logger m_logger;

      public ExceptionHandler(Logger logger) {
         m_logger = logger;
      }

      @Override
      public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
         m_logger.warn("Socket disconnected from " + e.getChannel().getRemoteAddress());
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
         e.getChannel().close();
      }
   }
}
