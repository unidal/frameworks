package org.unidal.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.plexus.logging.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.logger.LoggerFactory;
import org.unidal.tuple.Pair;

class SocketServerManager {
   private static ConcurrentMap<String, AtomicInteger> m_indexes = new ConcurrentHashMap<String, AtomicInteger>();

   private SocketHandler m_handler;

   private InetSocketAddress m_bindAddress;

   private int m_maxThreads;

   private String m_threadNamePrefix;

   private ServerBootstrap m_bootstrap;

   private ChannelGroup m_channelGroup = new DefaultChannelGroup();

   private Logger m_logger = LoggerFactory.getLogger(SocketServerManager.class);

   private boolean m_active;

   private org.unidal.net.SocketServerManager.MessageSender m_sender;

   public SocketServerManager(SocketHandler handler, int port, String host) {
      m_handler = handler;
      m_threadNamePrefix = handler.getClass().getSimpleName();

      if (host == null) {
         m_bindAddress = new InetSocketAddress(port);
      } else {
         m_bindAddress = new InetSocketAddress(host, port);
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

   public void setMaxThreads(int maxThreads) {
      m_maxThreads = maxThreads;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      m_threadNamePrefix = threadNamePrefix;
   }

   private ServerBootstrap setup(String group) {
      ChannelFactory factory;

      ThreadRenamingRunnable.setThreadNameDeterminer(ThreadNameDeterminer.CURRENT);

      if (m_maxThreads > 0) {
         ExecutorService bossExecutor = Threads.forPool().getFixedThreadPool(group + "-Boss-" + m_bindAddress, m_maxThreads);
         ExecutorService workerExecutor = Threads.forPool().getFixedThreadPool(group + "-Worker", m_maxThreads);

         factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
      } else {
         ExecutorService bossExecutor = Threads.forPool().getCachedThreadPool(group + "-Boss-" + m_bindAddress);
         ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(group + "-Worker");

         factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
      }

      ServerBootstrap bootstrap = new ServerBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
         @Override
         public ChannelPipeline getPipeline() {
            return Channels.pipeline(new ChannelDecoder(), new ChannelHandler());
         }
      });

      bootstrap.setOption("child.tcpNoDelay", true);
      bootstrap.setOption("child.keepAlive", true);
      bootstrap.bind(m_bindAddress);

      return bootstrap;
   }

   public void shutdown() {
      m_channelGroup.close().awaitUninterruptibly();
      m_bootstrap.getFactory().releaseExternalResources();
   }

   public void start() {
      String group = getUniquePrefix();

      m_active = true;
      m_bootstrap = setup(group);
      m_logger.info(m_threadNamePrefix + " is listening on " + m_bindAddress);

      m_sender = new MessageSender();

      Thread thread = Threads.forGroup(group).start(m_sender);

      try {
         while (!thread.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(1);
         }
      } catch (InterruptedException e) {
         // ignore it
      }
   }

   static class ChannelDecoder extends FrameDecoder {
      /**
       * return null means not all data is ready, so waiting for next network package.
       */
      @Override
      protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {
         int readableBytes = buffer.readableBytes();

         if (readableBytes < 4) {
            return null;
         }

         buffer.markReaderIndex();

         int length = buffer.readInt();

         buffer.resetReaderIndex();

         if (readableBytes < length + 4) {
            return null;
         }

         return buffer.readBytes(length + 4);
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
      public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
         m_channelGroup.add(event.getChannel());
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
                  Channel channel = message.getKey();

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
