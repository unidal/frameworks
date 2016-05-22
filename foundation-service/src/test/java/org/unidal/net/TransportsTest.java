package org.unidal.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.net.transport.codec.FrameMessageDecoder;
import org.unidal.net.transport.codec.FrameMessageEncoder;

public class TransportsTest {
   private static ConcurrentMap<String, AtomicInteger> MAP = new ConcurrentHashMap<String, AtomicInteger>();

   @Test
   public void asClient() {
      Transports.asClient().name("API").connect("localhost", 1234) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .withThreads(10) //
            .handler("encoder", new MockMessageEncoder()) //
            .start();
   }

   @Test
   public void asServer() {
      Transports.asServer().name("API").bind(1234) //
            .option(ChannelOption.SO_REUSEADDR, true) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
            .withBossThreads(1).withWorkerThreads(10) //
            .handler("decoder", new MockMessageDecoder()) //
            .handler("message", new MockMessageHandler()) //
            .start();
   }

   @Test
   public void test() throws Exception {
      ServerTransport st = Transports.asServer().name("Cat").bind(2345) //
            .option(ChannelOption.SO_REUSEADDR, true) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
            .handler("decoder", new MockMessageDecoder()) //
            .handler("encoder", new MockMessageEncoder()) //
            .handler("message", new MockMessageHandler()) //
            .start();

      List<ClientTransport> cts = new ArrayList<ClientTransport>();

      for (int j = 0; j < 3; j++) {
         ClientTransport ct = Transports.asClient().name("Cat").connect("localhost", 2345) //
               .option(ChannelOption.TCP_NODELAY, true) //
               .option(ChannelOption.SO_KEEPALIVE, true) //
               .handler("decoder", new MockMessageDecoder()) //
               .handler("encoder", new MockMessageEncoder()) //
               .handler("message", new MockMessageHandler()) //
               .start();

         for (int i = 0; i < 10; i++) {
            ct.write("PING " + j + ":" + i);
         }

         cts.add(ct);
      }

      Thread.sleep(100);

      // st.write("Hello");
      st.stop(300, TimeUnit.MILLISECONDS);

      for (ClientTransport ct : cts) {
         ct.stop(100, TimeUnit.MILLISECONDS);
      }

      AtomicInteger pings = MAP.get("PING");
      AtomicInteger pongs = MAP.get("PONG");

      System.out.println(MAP);
      Assert.assertEquals(pings.toString(), pongs.toString());
   }

   static class MockMessageDecoder extends FrameMessageDecoder<String> implements Cloneable {
      @Override
      public Object clone() throws CloneNotSupportedException {
         return super.clone();
      }

      @Override
      protected String frameToMessage(ChannelHandlerContext ctx, ByteBuf frame) {
         byte[] data = new byte[frame.readableBytes()];

         frame.readBytes(data);
         return new String(data);
      }
   }

   static class MockMessageEncoder extends FrameMessageEncoder<String> implements Cloneable {
      @Override
      protected void messageToFrame(ChannelHandlerContext ctx, String msg, ByteBuf frame) {
         frame.writeBytes(msg.getBytes());
      }
   }

   static class MockMessageHandler extends ChannelInboundHandlerAdapter implements Cloneable {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         Channel channel = ctx.channel();
         String str = String.valueOf(msg);
         // System.out.println("Message received from " + channel.remoteAddress() + ", content: " + str);

         if (str.startsWith("PING ")) {
            AtomicInteger count = new AtomicInteger();
            AtomicInteger c;

            if ((c = MAP.putIfAbsent("PING", count)) != null) {
               count = c;
            }

            count.incrementAndGet();
            channel.writeAndFlush("PONG " + str.substring(4));
         } else if (str.startsWith("PONG ")) {
            AtomicInteger count = new AtomicInteger();
            AtomicInteger c;

            if ((c = MAP.putIfAbsent("PONG", count)) != null) {
               count = c;
            }

            count.incrementAndGet();
         }
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         ctx.channel().close();
         cause.printStackTrace();
      }
   }
}
