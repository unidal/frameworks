package org.unidal.net.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.helper.Reflects;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.net.TransportRepository;

@Named(type = ServerTransportHandler.class, instantiationStrategy = Named.PER_LOOKUP)
public class ServerTransportHandler implements Task, LogEnabled {
   @Inject
   private TransportRepository m_repository;

   private ServerTransportDescriptor m_descriptor;

   private AtomicBoolean m_active = new AtomicBoolean(true);

   private CountDownLatch m_latch = new CountDownLatch(1);

   private Logger m_logger;

   private Channel m_channel;

   public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      m_latch.await(timeout, unit);
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   @Override
   public String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public void run() {
      ServerBootstrap bootstrap = new ServerBootstrap();
      InetSocketAddress localAddress = m_descriptor.getLocalAddress();
      EventLoopGroup bossGroup = m_descriptor.getBossGroup();
      EventLoopGroup workerGroup = m_descriptor.getGroup();
      Class<? extends ServerChannel> channelClass = m_descriptor.getChannelClass();

      bootstrap.group(bossGroup, workerGroup).channel(channelClass);
      bootstrap.childHandler(new ServerChannelInitializer());

      for (Map.Entry<ChannelOption<Object>, Object> e : m_descriptor.getOptions().entrySet()) {
         bootstrap.childOption(e.getKey(), e.getValue());
      }

      try {
         ChannelFuture future = bootstrap.bind(localAddress).sync();

         if (future.isDone()) {
            String address = localAddress.getAddress().getHostAddress();
            int port = localAddress.getPort();

            m_logger.info(String.format("%s server is listening on %s:%s", m_descriptor.getName(), address, port));
         }

         m_channel = future.channel();
         run0();
      } catch (Throwable e) {
         m_logger.error(e.getMessage(), e);
      } finally {
         bossGroup.shutdownGracefully();
         workerGroup.shutdownGracefully();
      }

      if (m_channel != null) {
         m_channel.close();
      }

      m_latch.countDown();
   }

   private void run0() throws InterruptedException {
      while (m_active.get()) {
         TimeUnit.MILLISECONDS.sleep(1);
      }

      long end = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3); // 3s timeout

      while (!m_repository.isEmpty()) {
         if (System.currentTimeMillis() >= end) {
            throw new InterruptedException("Timeout while repository still have messages!");
         }

         TimeUnit.MILLISECONDS.sleep(1);
      }
   }

   public void setDescriptor(ServerTransportDescriptor descriptor) {
      m_descriptor = descriptor;
   }

   @Override
   public void shutdown() {
      m_active.set(false);
   }

   public boolean write(Object message) {
      if (m_active.get()) {
         return m_repository.put(message);
      } else {
         return false;
      }
   }

   private static class ServerChannelManager extends ChannelInboundHandlerAdapter {
      private ChannelGroup m_group;

      public ServerChannelManager(ChannelGroup group) {
         m_group = group;
      }

      @Override
      public void channelActive(ChannelHandlerContext ctx) throws Exception {
         m_group.add(ctx.channel());

         super.channelActive(ctx);
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
         super.channelInactive(ctx);

         m_group.remove(ctx.channel());
      }
   }

   class ServerChannelInitializer extends ChannelInitializer<Channel> {
      @Override
      protected void initChannel(Channel ch) throws Exception {
         ChannelPipeline pipeline = ch.pipeline();

         for (Map.Entry<String, ChannelHandler> e : m_descriptor.getHandlers().entrySet()) {
            String name = e.getKey();
            ChannelHandler handler = e.getValue();

            if (handler instanceof Cloneable) {
               Method method = Reflects.forMethod().getDeclaredMethod(Object.class, "clone");

               method.setAccessible(true);
               pipeline.addLast(name, (ChannelHandler) method.invoke(handler));
            } else {
               pipeline.addLast(name, handler);
            }
         }
      }
   }
}
