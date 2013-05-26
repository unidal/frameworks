package org.unidal.socket.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.socket.Message;
import org.unidal.socket.MessageCodec;
import org.unidal.socket.MessageInboundHandler;
import org.unidal.socket.SocketListener;
import org.unidal.socket.SocketServer;

public class TcpSocketServer implements SocketServer {
   @Inject
   private MessageCodec<Message> m_codec;

   @Inject
   private SocketListener m_listener;

   @Inject
   private String m_name;

   @Inject
   private int m_queueCapacity = 10000;

   @Inject
   private int m_decodeThreads = 1;

   private BlockingQueue<ChannelBuffer> m_queue;

   private ServerBootstrap m_bootstrap;

   private ChannelGroup m_channelGroup = new DefaultChannelGroup();

   private MessageInboundHandler<Message> m_handler;

   @Override
   public void listenOn(InetSocketAddress address) {
      ExecutorService bossExecutor = Threads.forPool().getCachedThreadPool(m_name + "-Boss-" + address);
      ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(m_name + "-Worker");
      ChannelFactory factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
      ServerBootstrap bootstrap = new ServerBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
         @Override
         public ChannelPipeline getPipeline() {
            return Channels.pipeline(new SegmentHandler(), new EnqueueHandler());
         }
      });

      bootstrap.setOption("child.tcpNoDelay", true);
      bootstrap.setOption("child.keepAlive", true);
      bootstrap.bind(address);

      m_queue = new LinkedBlockingQueue<ChannelBuffer>(m_queueCapacity);
      m_bootstrap = bootstrap;

      for (int i = 0; i < m_decodeThreads; i++) {
         Threads.forGroup(m_name).start(new DecodingTask(i));
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Message> void onMessage(MessageInboundHandler<T> handler) {
      m_handler = (MessageInboundHandler<Message>) handler;
   }

   @SuppressWarnings("unchecked")
   public void setCodec(MessageCodec<? extends Message> codec) {
      m_codec = (MessageCodec<Message>) codec;
   }

   public void setName(String name) {
      m_name = name;
   }

   protected class DecodingTask implements Task {
      private int m_index;

      private boolean m_active = true;

      public DecodingTask(int index) {
         m_index = index;
      }

      @Override
      public String getName() {
         return m_name + "-" + getClass().getSimpleName() + "-" + m_index;
      }

      @Override
      public void run() {
         try {
            while (m_active) {
               ChannelBuffer buf = m_queue.poll(1, TimeUnit.MILLISECONDS);

               if (buf != null) {
                  try {
                     Message message = m_codec.decode(buf);

                     m_handler.handle(message);
                  } catch (Throwable e) {
                     if (m_listener != null) {
                        buf.resetReaderIndex();
                        m_listener.onReceivingFailure(buf, e);
                     } else {
                        e.printStackTrace();
                     }
                  }
               }
            }
         } catch (InterruptedException e) {
            // ignore it
         } finally {
            if (m_bootstrap != null) {
               m_channelGroup.close().awaitUninterruptibly();
               m_bootstrap.releaseExternalResources();
               m_bootstrap = null;
            }
         }
      }

      @Override
      public void shutdown() {
         m_active = false;
      }
   }

   protected class EnqueueHandler extends SimpleChannelHandler {
      @Override
      public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
         m_channelGroup.add(event.getChannel());
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) {
         event.getChannel().close();
      }

      @Override
      public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
         ChannelBuffer buf = (ChannelBuffer) event.getMessage();

         if (!m_queue.offer(buf)) {
            if (m_listener != null) {
               m_listener.onReceivingOverflowed(buf);
            }
         } else {
            if (m_listener != null) {
               m_listener.onReceived(buf);
            }
         }
      }
   }

   protected static class SegmentHandler extends FrameDecoder {
      /**
       * return null means not all data is ready, so waiting for next network package.
       */
      @Override
      protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {
         if (buffer.readableBytes() < 4) {
            return null;
         }

         buffer.markReaderIndex();

         int length = buffer.readInt();
         int current = buffer.readableBytes();

         buffer.resetReaderIndex();

         if (current < length) {
            return null;
         } else {
            return buffer.readBytes(length + 4);
         }
      }
   }
}
