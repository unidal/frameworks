package org.unidal.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.net.transport.AbstractTransportHub;
import org.unidal.net.transport.TransportHub;
import org.unidal.net.transport.codec.FrameMessageDecoder;

public class TransportsTest {
   private static ConcurrentMap<String, AtomicInteger> MAP = new ConcurrentHashMap<String, AtomicInteger>();

   @Test
   public void asClient() {
      Transports.asClient().name("API").connect("localhost", 1234) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .withThreads(10) //
            .start(new MockTransportHub());
   }

   @Test
   public void asServer() {
      Transports.asServer().name("API").bind(1234) //
            .option(ChannelOption.SO_REUSEADDR, true) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
            .withBossThreads(1).withWorkerThreads(10) //
            .start(new MockTransportHub());
   }

   @Test
   public void test() throws Exception {
      MockTransportHub hub = new MockTransportHub();
      ServerTransport st = Transports.asServer().name("Cat").bind(2345) //
            .option(ChannelOption.SO_REUSEADDR, true) //
            .option(ChannelOption.TCP_NODELAY, true) //
            .option(ChannelOption.SO_KEEPALIVE, true) //
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
            .start(hub);

      List<ClientTransport> cts = new ArrayList<ClientTransport>();

      for (int j = 0; j < 3; j++) {
         ClientTransport ct = Transports.asClient().name("Cat").connect("localhost", 2345) //
               .option(ChannelOption.TCP_NODELAY, true) //
               .option(ChannelOption.SO_KEEPALIVE, true) //
               .start(hub);

         for (int i = 0; i < 10; i++) {
            hub.write("PING " + j + ":" + i);
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

   static class MockTransportHub extends AbstractTransportHub<String> implements TransportHub {
      private BlockingQueue<String> m_queue = new ArrayBlockingQueue<String>(100);

      @Override
      protected String decode(ByteBuf buf) {
         int size = buf.readableBytes();
         byte[] data = new byte[size];

         buf.readBytes(data);

         String message = new String(data);
         return message;
      }

      @Override
      protected void encode(ByteBuf buf, String message) {
         buf.writeBytes(message.getBytes());
      }

      @Override
      protected void handle(String message, Channel channel) {
         if (message.startsWith("PING ")) {
            AtomicInteger count = new AtomicInteger();
            AtomicInteger c;

            if ((c = MAP.putIfAbsent("PING", count)) != null) {
               count = c;
            }

            count.incrementAndGet();

            write("PONG " + message.substring(5));
         } else if (message.startsWith("PONG ")) {
            AtomicInteger count = new AtomicInteger();
            AtomicInteger c;

            if ((c = MAP.putIfAbsent("PONG", count)) != null) {
               count = c;
            }

            count.incrementAndGet();
         }
      }

      @Override
      protected String nextMessage() {
         return m_queue.poll();
      }

      public boolean write(String message) {
         return m_queue.offer(message);
      }
   }
}
