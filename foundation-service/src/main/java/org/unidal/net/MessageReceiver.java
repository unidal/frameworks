package org.unidal.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

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
import org.unidal.lookup.logger.LoggerFactory;

@Deprecated
class MessageReceiver {
   private MessageDelegate m_delegate;

   private int m_port;

   private String m_host;

   private int m_maxThreads;

   private String m_threadNamePrefix;

   private ServerBootstrap m_bootstrap;

   private ChannelGroup m_channelGroup = new DefaultChannelGroup();

   private Logger m_logger = LoggerFactory.getLogger(MessageReceiver.class);

   public MessageReceiver(MessageDelegate delegate, int port, String host) {
      m_delegate = delegate;
      m_port = port;
      m_host = host;
      m_threadNamePrefix = delegate.getClass().getSimpleName();
   }

   public void setMaxThreads(int maxThreads) {
      m_maxThreads = maxThreads;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      m_threadNamePrefix = threadNamePrefix;
   }

   public void shutdown() {
      m_channelGroup.close().awaitUninterruptibly();
      m_bootstrap.getFactory().releaseExternalResources();
   }

   public void startServer() {
      InetSocketAddress address;
      ChannelFactory factory;

      ThreadRenamingRunnable.setThreadNameDeterminer(ThreadNameDeterminer.CURRENT);

      if (m_host == null) {
         address = new InetSocketAddress(m_port);
      } else {
         address = new InetSocketAddress(m_host, m_port);
      }

      String name;

      if (m_threadNamePrefix == null) {
         String className = new Exception().getStackTrace()[2].getClassName();
         int pos = className.lastIndexOf('.');

         name = className.substring(pos + 1);
      } else {
         name = m_threadNamePrefix;
      }

      if (m_maxThreads > 0) {
         ExecutorService bossExecutor = Threads.forPool().getFixedThreadPool(name + "Boss" + address, m_maxThreads);
         ExecutorService workerExecutor = Threads.forPool().getFixedThreadPool(name + "Worker", m_maxThreads);

         factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
      } else {
         ExecutorService bossExecutor = Threads.forPool().getCachedThreadPool(name + "Boss-" + address);
         ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(name + "Worker");

         factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
      }

      ServerBootstrap bootstrap = new ServerBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
         @Override
         public ChannelPipeline getPipeline() {
            return Channels.pipeline(new MessageDecoder(), new ChannelHandler());
         }
      });

      bootstrap.setOption("child.tcpNoDelay", true);
      bootstrap.setOption("child.keepAlive", true);
      bootstrap.bind(address);

      m_bootstrap = bootstrap;
      m_logger.info(m_threadNamePrefix + " is listening at " + address);
   }

   class ChannelHandler extends SimpleChannelHandler {
      @Override
      public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
         m_channelGroup.add(event.getChannel());
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) {
         m_logger.warn(event.getChannel().toString(), event.getCause());

         event.getChannel().close();
      }

      @Override
      public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
         ChannelBuffer buffer = (ChannelBuffer) event.getMessage();

         m_delegate.onMessageReceived(buffer);
      }
   }

   public static class MessageDecoder extends FrameDecoder {
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
}
