package org.unidal.socket.tcp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
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
import org.unidal.lookup.annotation.Inject;
import org.unidal.socket.Message;
import org.unidal.socket.MessageCodec;
import org.unidal.socket.MessageOutboundHandler;
import org.unidal.socket.SocketClient;
import org.unidal.socket.SocketListener;

public class TcpSocketClient implements SocketClient {
   @Inject
   private MessageCodec<Message> m_codec;

   @Inject
   private SocketListener m_listener;

   @Inject
   private String m_name;

   @Inject
   private int m_queueCapacity = 10000;

   private BlockingQueue<Entry> m_queue;

   private FailoverChannelManager m_manager;

   @Override
   public void connectTo(InetSocketAddress... addresses) {
      ExecutorService bossExecutor = Threads.forPool().getFixedThreadPool(m_name + "-Boss", 2);
      ExecutorService workerExecutor = Threads.forPool().getFixedThreadPool(m_name + "-Worker", 10);
      ChannelFactory factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
      ClientBootstrap bootstrap = new ClientBootstrap(factory);

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
         @Override
         public ChannelPipeline getPipeline() {
            return Channels.pipeline(new ChannelDisconnectedHandler());
         }
      });

      bootstrap.setOption("tcpNoDelay", true);
      bootstrap.setOption("keepAlive", true);

      m_manager = new FailoverChannelManager(bootstrap, Arrays.asList(addresses));
      m_queue = new LinkedBlockingQueue<Entry>(m_queueCapacity);

      Threads.forGroup(m_name).start(m_manager);
      Threads.forGroup(m_name).start(new AsynchronousMessageSender());
   }

   @Override
   public void send(Message message) {
      send(message, null, null);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Message> void send(T message, MessageOutboundHandler<T> handler, Object context) {
      Entry entry = new Entry(message, (MessageOutboundHandler<Message>) handler, context);

      if (!m_queue.offer(entry)) {
         if (m_listener != null) {
            m_listener.onSendingOverflowed(message);
         }

         entry.onSendingOverflowed();
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

      private boolean checkWritable(ChannelFuture future) {
         boolean isWriteable = false;

         if (future != null && future.getChannel().isOpen()) {
            if (future.getChannel().isWritable()) {
               isWriteable = true;
            } else {
               int count = m_attempts.incrementAndGet();

               if (m_listener != null) {
                  m_listener.onWriteBufferFull(count);
               }
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
               ChannelFuture future = m_manager.getChannelFuture();

               if (checkWritable(future)) {
                  Entry entry = m_queue.poll();

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
         ChannelFuture future = m_manager.getChannelFuture();
         ChannelBuffer buf = m_codec.encode(message);
         int length = buf.readableBytes();

         buf.markReaderIndex();
         buf.setInt(0, length - 4);
         future.getChannel().write(buf);
         buf.resetReaderIndex();

         return buf;
      }

      @Override
      public void shutdown() {
         m_active = false;
      }
   }

   protected class ChannelDisconnectedHandler extends SimpleChannelHandler {
      @Override
      public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
         if (m_listener != null) {
            m_listener.onDisconnected((InetSocketAddress) e.getChannel().getRemoteAddress());
         }
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
         e.getChannel().close();
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

      public void onError(Throwable t) {
         if (m_handler != null) {
            try {
               m_handler.onError(m_message, t, m_context);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }
      }

      public void onSendingOverflowed() {
         if (m_handler != null) {
            try {
               m_handler.onSendingOverflowed(m_message, m_context);
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

   protected class FailoverChannelManager implements Task {
      private List<InetSocketAddress> m_serverAddresses;

      private ClientBootstrap m_bootstrap;

      private int m_activeIndex;

      private ChannelFuture m_activeFuture;

      private ChannelFuture m_lastFuture;

      private boolean m_active = true;

      public FailoverChannelManager(ClientBootstrap bootstrap, List<InetSocketAddress> serverAddresses) {
         m_bootstrap = bootstrap;
         m_serverAddresses = serverAddresses;
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
         return m_name + "-" + getClass().getSimpleName();
      }

      @Override
      public void run() {
         int len = m_serverAddresses.size();
         List<ChannelFuture> futures = new ArrayList<ChannelFuture>(Collections.<ChannelFuture> nCopies(len, null));

         for (int i = 0; i < len; i++) {
            ChannelFuture future = tryCreateChannel(i);

            if (future != null) {
               m_activeFuture = future;
               m_activeIndex = i;
               break;
            }
         }

         try {
            while (m_active) {
               if (m_activeFuture != null && !m_activeFuture.getChannel().isOpen()) {
                  m_activeIndex = m_serverAddresses.size();
               }

               for (int i = 0; i < m_activeIndex; i++) {
                  ChannelFuture future = tryCreateChannel(i);

                  if (future != null) {
                     m_lastFuture = m_activeFuture;
                     m_activeFuture = future;
                     m_activeIndex = i;
                     break;
                  }
               }

               Thread.sleep(1000L); // check every second
            }
         } catch (InterruptedException e) {
            // ignore
         } finally {
            for (ChannelFuture future : futures) {
               if (future != null) {
                  future.getChannel().getCloseFuture().awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100ms
               }
            }

            m_bootstrap.releaseExternalResources();
         }
      }

      @Override
      public void shutdown() {
         m_active = false;
      }

      private ChannelFuture tryCreateChannel(int index) {
         InetSocketAddress address = m_serverAddresses.get(index);
         ChannelFuture future = m_bootstrap.connect(address);

         future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100 ms

         if (!future.isSuccess()) {
            future.getChannel().getCloseFuture().awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100ms

            if (m_listener != null) {
               m_listener.onConnectionFailure(address, future.getCause());
            }

            return null;
         } else {
            if (m_listener != null) {
               m_listener.onConnectionSuccess(address);
            }

            return future;
         }
      }
   }
}
