package org.unidal.socket.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
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
import org.jboss.netty.channel.socket.oio.OioDatagramChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.socket.Message;
import org.unidal.socket.MessageCodec;
import org.unidal.socket.MessageInboundHandler;
import org.unidal.socket.MessageOutboundHandler;
import org.unidal.socket.SocketClient;
import org.unidal.socket.SocketListener;
import org.unidal.socket.SocketServer;

public class UdpSocket implements SocketClient, SocketServer {
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

   private BlockingQueue<Entry> m_outQueue;

   private BlockingQueue<ChannelBuffer> m_inQueue;

   private MessageInboundHandler<Message> m_handler;

   private ConnectionlessBootstrap m_bootstrap;

   private Channel m_channel;

   private InetSocketAddress m_address;

   @Override
   public void connectTo(InetSocketAddress... addresses) {
      throw new UnsupportedOperationException("UDP does not need a connection!");
   }

   @Override
   public void listenOn(InetSocketAddress address) {
      ExecutorService workerExecutor = Threads.forPool().getCachedThreadPool(m_name + "-Worker");
      ChannelFactory factory = new OioDatagramChannelFactory(workerExecutor);
      ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
         @Override
         public ChannelPipeline getPipeline() {
            return Channels.pipeline(new SegmentHandler(), new EnqueueHandler());
         }
      });

      m_bootstrap = bootstrap;
      m_address = address;
      m_inQueue = new LinkedBlockingQueue<ChannelBuffer>(m_queueCapacity);
      m_outQueue = new LinkedBlockingQueue<Entry>(m_queueCapacity);
      m_channel = bootstrap.bind(new InetSocketAddress(address.getPort()));

      Threads.forGroup(m_name).start(new AsynchronousMessageSender());

      for (int i = 0; i < m_decodeThreads; i++) {
         Threads.forGroup(m_name).start(new DecodingTask(i));
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Message> void onMessage(MessageInboundHandler<T> handler) {
      m_handler = (MessageInboundHandler<Message>) handler;
   }

   @Override
   public void send(Message message) {
      send(message, null, null);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Message> void send(T message, MessageOutboundHandler<T> handler, Object context) {
      Entry entry = new Entry(message, (MessageOutboundHandler<Message>) handler, context);

      if (!m_outQueue.offer(entry)) {
         if (m_listener != null) {
            m_listener.onSendingOverflowed(message);
         }

         entry.onDiscarded();
      }
   }

   @SuppressWarnings("unchecked")
   public void setCodec(MessageCodec<? extends Message> codec) {
      m_codec = (MessageCodec<Message>) codec;
   }

   public void setListener(SocketListener listener) {
      m_listener = listener;
   }

   public void setName(String name) {
      m_name = name;
   }

   public void setQueueCapacity(int queueCapacity) {
      m_queueCapacity = queueCapacity;
   }

   protected class AsynchronousMessageSender implements Task {
      private boolean m_active = true;

      private AtomicInteger m_attempts = new AtomicInteger();

      private boolean checkWritable() {
         boolean isWriteable = false;

         if (m_channel.isWritable()) {
            isWriteable = true;
         } else {
            int count = m_attempts.incrementAndGet();

            if (m_listener != null) {
               m_listener.onWriteBufferFull(count);
            }
         }

         return isWriteable;
      }

      @Override
      public String getName() {
         return m_name + "-" + getClass().getSimpleName();
      }

      @Override
      public void run() {
         try {
            while (m_active) {
               if (checkWritable()) {
                  Entry entry = m_outQueue.poll();

                  if (entry != null) {
                     Message message = entry.getMessage();

                     try {
                        ChannelBuffer buf = sendInternal(message);

                        if (m_listener != null) {
                           m_listener.onSent(buf);
                        }

                        entry.onSent();
                     } catch (Throwable t) {
                        if (m_listener != null) {
                           m_listener.onSendingFailure(message, t);
                        }

                        entry.onError(t);
                     }
                  }
               } else {
                  TimeUnit.MILLISECONDS.sleep(5);
               }
            }
         } catch (InterruptedException e) {
            // ignore it
         }
      }

      private ChannelBuffer sendInternal(Message message) {
         ChannelBuffer buf = m_codec.encode(message);
         int length = buf.readableBytes();

         buf.markReaderIndex();
         buf.setInt(0, length - 4);
         m_channel.write(buf, m_address);
         buf.resetReaderIndex();

         return buf;
      }

      @Override
      public void shutdown() {
         m_active = false;
      }
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
               ChannelBuffer buf = m_inQueue.poll(1, TimeUnit.MILLISECONDS);

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
      public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
         if (m_listener != null) {
            m_listener.onDisconnected((InetSocketAddress) e.getChannel().getRemoteAddress());
         }
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) {
         event.getChannel().close();
      }

      @Override
      public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
         ChannelBuffer buf = (ChannelBuffer) event.getMessage();

         if (!m_inQueue.offer(buf)) {
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

   protected static class Entry {
      private Message m_message;

      private MessageOutboundHandler<Message> m_handler;

      private Object m_context;

      public Entry(Message message, MessageOutboundHandler<Message> handler, Object context) {
         m_message = message;
         m_handler = handler;
         m_context = context;
      }

      public Object getContext() {
         return m_context;
      }

      public MessageOutboundHandler<Message> getHandler() {
         return m_handler;
      }

      public Message getMessage() {
         return m_message;
      }

      public void onDiscarded() {
         if (m_handler != null) {
            try {
               m_handler.onSendingOverflowed(m_message, m_context);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }
      }

      public void onError(Throwable t) {
         if (m_handler != null) {
            try {
               m_handler.onError(m_message, t, m_context);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }
      }

      public void onSent() {
         if (m_handler != null) {
            try {
               m_handler.onSent(m_message, m_context);
            } catch (Throwable e) {
               e.printStackTrace();
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
