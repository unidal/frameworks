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
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

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
import org.unidal.lookup.annotation.Named;
import org.unidal.net.transport.handler.ServerStateHandler;

@Named(type = ServerTransportHandler.class, instantiationStrategy = Named.PER_LOOKUP)
public class ServerTransportHandler implements Task, LogEnabled {
   private ServerTransportDescriptor m_descriptor;

   private ChannelGroup m_channelGroup = new DefaultChannelGroup("Cat", GlobalEventExecutor.INSTANCE);

   private AtomicBoolean m_active = new AtomicBoolean(true);

   private CountDownLatch m_latch = new CountDownLatch(1);

   private CountDownLatch m_warmup = new CountDownLatch(1);

   private Logger m_logger;

   public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      m_latch.await(timeout, unit);
   }

   public void awaitWarmup() throws InterruptedException {
      m_warmup.await();
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
      try {
         ServerBootstrap bootstrap = new ServerBootstrap();
         InetSocketAddress localAddress = m_descriptor.getLocalAddress();
         Class<? extends ServerChannel> channelClass = m_descriptor.getChannelClass();

         bootstrap.group(m_descriptor.getBossGroup(), m_descriptor.getGroup()).channel(channelClass);
         bootstrap.childHandler(new ServerChannelInitializer());

         for (Map.Entry<ChannelOption<Object>, Object> e : m_descriptor.getOptions().entrySet()) {
            bootstrap.childOption(e.getKey(), e.getValue());
         }

         ChannelFuture future = bootstrap.bind(localAddress).sync();

         if (future.isSuccess()) {
            String address = localAddress.getAddress().getHostAddress();
            int port = localAddress.getPort();

            m_warmup.countDown();
            m_logger.info(String.format("%s server is listening on %s:%s", m_descriptor.getName(), address, port));
         }

         Channel channel = future.channel();

         channel.closeFuture().sync();
      } catch (Throwable e) {
         m_logger.error(e.getMessage(), e);
      } finally {
         m_descriptor.getBossGroup().shutdownGracefully();
         m_descriptor.getGroup().shutdownGracefully();
         m_latch.countDown();
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
         // broadcast directly
         m_channelGroup.writeAndFlush(message);
         return true;
      } else {
         return false;
      }
   }

   private class ChannelGroupHandler extends ChannelInboundHandlerAdapter {
      @Override
      public void channelActive(ChannelHandlerContext ctx) throws Exception {
         Channel channel = ctx.channel();

         m_channelGroup.add(channel);
         super.channelActive(ctx);
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
         Channel channel = ctx.channel();

         m_channelGroup.remove(channel);
         super.channelInactive(ctx);
      }
   }

   private class ServerChannelInitializer extends ChannelInitializer<Channel> {
      @Override
      protected void initChannel(Channel ch) throws Exception {
         ChannelPipeline pipeline = ch.pipeline();

         pipeline.addLast(new ChannelGroupHandler());
         pipeline.addLast(new ServerStateHandler(m_descriptor.getName()));

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
