package org.unidal.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.logger.LoggerFactory;
import org.unidal.tuple.Pair;

class SocketClientManager {
   private static final ConcurrentMap<String, AtomicInteger> m_indexes = new ConcurrentHashMap<String, AtomicInteger>();

   private SocketHandler m_handler;

   private int m_maxThreads;

   private String m_threadNamePrefix;

   private FailoverChannelManager m_manager;

   private boolean m_active;

   private List<InetSocketAddress> m_serverAddresses = new ArrayList<InetSocketAddress>();

   private Logger m_logger = LoggerFactory.getLogger(SocketClientManager.class);

   private MessageSender m_sender;

   private int m_checkInterval;

   private String m_group;

   public SocketClientManager(SocketHandler handler, List<Integer> ports, List<String> servers) {
      m_handler = handler;

      int len = ports.size();

      for (int i = 0; i < len; i++) {
         String server = servers.get(i);
         int port = ports.get(i);

         m_serverAddresses.add(new InetSocketAddress(server, port));
      }
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

   public void setCheckInterval(int checkInterval) {
      m_checkInterval = checkInterval;
   }

   public void setMaxThreads(int maxThreads) {
      m_maxThreads = maxThreads;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      m_threadNamePrefix = threadNamePrefix;
   }

   public void shutdown() {
      m_active = false;

      if (m_sender != null) {
         m_sender.shutdown();
      }

      m_manager.shutdown();
   }

   public void start() {
      m_group = getUniquePrefix();
      m_active = true;
      m_manager = new FailoverChannelManager();
      m_sender = new MessageSender();
      
      Threads.forGroup(m_group).start(m_manager);
      Thread thread = Threads.forGroup(m_group).start(m_sender);

      try {
         while (!thread.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(1);
         }
      } catch (InterruptedException e) {
         // ignore it
      }
   }

   class ChannelHandler extends SimpleChannelHandler {
      @Override
      public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
         super.channelConnected(ctx, event);

         m_handler.onConnected(event.getChannel());
      }

      @Override
      public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
         super.channelDisconnected(ctx, event);

         m_handler.onDisconnected(event.getChannel());
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) throws Exception {
         m_handler.onException(event.getChannel(), event.getCause());
      }

      @Override
      public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
         ChannelBuffer buffer = (ChannelBuffer) event.getMessage();

         m_handler.onMessage(event.getChannel(), buffer);
      }
   }

   class FailoverChannelManager implements Task {
      private ClientBootstrap m_bootstrap;

      private ChannelFuture m_activeFuture;

      private int m_activeIndex = -1;

      private ChannelFuture m_lastFuture;

      private AtomicInteger m_attempts = new AtomicInteger();

      private AtomicLong m_lastTime = new AtomicLong();

      public FailoverChannelManager() {
         m_bootstrap = setup(m_group);

         int len = m_serverAddresses.size();

         for (int i = 0; i < len; i++) {
            ChannelFuture future = createChannel(i);

            if (future != null) {
               m_activeFuture = future;
               m_activeIndex = i;
               break;
            }
         }
      }

      private ChannelFuture createChannel(int index) {
         InetSocketAddress address = m_serverAddresses.get(index);
         ChannelFuture future = m_bootstrap.connect(address);

         future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100 ms

         if (!future.isSuccess()) {
            future.getChannel().close();

            int attempts = m_attempts.incrementAndGet();
            long lastTime = m_lastTime.get();
            long now = System.currentTimeMillis();

            if (attempts == 1 || attempts % 100 == 0 || lastTime < now - 60 * 1000L) {
               m_lastTime.set(now);
               m_logger.warn("Error when connecting to " + address + ", " + future.getCause() + ", attempts: " + attempts);
            }

            return null;
         } else {
            return future;
         }
      }

      public ChannelFuture getChannelFuture() {
         if (m_lastFuture != null && m_lastFuture != m_activeFuture) {
            m_lastFuture.getChannel().close();
            m_lastFuture = null;
         }

         return m_activeFuture;
      }

      @Override
      public String getName() {
         return SocketClientManager.this.getClass().getSimpleName() + "-" + getClass().getSimpleName();
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

               Thread.sleep(m_checkInterval);
            }
         } catch (InterruptedException e) {
            // ignore
         }

         shutdown();
      }

      private ClientBootstrap setup(String group) {
         NioClientSocketChannelFactory factory;

         if (m_maxThreads > 0) {
            ExecutorService bossExecutor = Threads.forPool().getFixedThreadPool(group + "-Boss", 10);
            ExecutorService workerExecutor = Threads.forPool().getFixedThreadPool(group + "-Worker", m_maxThreads);
            factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
         } else {
            ExecutorService bossExecutor = Threads.forPool().getCachedThreadPool(group + "-Boss");
            ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(group + "-Worker");
            factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
         }

         ClientBootstrap bootstrap = new ClientBootstrap(factory);

         bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
               return Channels.pipeline(new ChannelHandler());
            }
         });

         bootstrap.setOption("tcpNoDelay", true);
         bootstrap.setOption("keepAlive", true);
         bootstrap.setOption("connectTimeoutMillis", 2000);

         return bootstrap;
      }

      @Override
      public void shutdown() {
         if (m_activeFuture != null) {
            m_activeFuture.getChannel().close().awaitUninterruptibly();
            m_activeFuture = null;
         }

         m_bootstrap.getFactory().releaseExternalResources();
      }
   }

   class MessageSender implements Task {
      private AtomicInteger m_attempts = new AtomicInteger();

      private boolean checkWritable(Channel channel) {
         boolean isWriteable = false;

         if (channel != null && channel.isOpen()) {
            if (channel.isWritable()) {
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

      @Override
      public void run() {
         try {
            while (m_active) {
               Pair<Channel, ChannelBuffer> message = m_handler.getNextMessage();

               if (message != null) {
                  ChannelFuture future = m_manager.getChannelFuture();
                  Channel channel = future == null ? null : future.getChannel();

                  if (channel != null && checkWritable(channel)) {
                     try {
                        channel.write(message.getValue());
                     } catch (Throwable t) {
                        m_logger.error("Error when sending message over TCP socket!", t);
                     }
                  }
               }

               TimeUnit.MILLISECONDS.sleep(5);
            }
         } catch (InterruptedException e) {
            // ignore it
         }
      }

      @Override
      public void shutdown() {
      }
   }
}
