package org.unidal.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.net.Sockets.Client;
import org.unidal.net.Sockets.Server;
import org.unidal.tuple.Pair;

public class SocketsTest extends ComponentTestCase {
   @BeforeClass
   public static void beforeClass() {
      System.setProperty("devMode", "true");
   }

   @Test
   public void test() throws Exception {
      MockHandler ch = new MockHandler();
      MockHandler sh = new MockHandler();
      Server s1 = Sockets.asServer().threads("Server", 0).listenOn(9444).start(sh);
      Server s2 = Sockets.asServer().threads("Server", 0).listenOn(9445).start(sh);
      Client c = Sockets.asClient().threads("Client", 0).checkInterval(10) //
            .connectTo("localhost", 9444) //
            .connectTo("localhost", 9445) //
            .start(ch);

      ch.send("Hello server!");
      sh.check("Hello server!");

      sh.send("Hello client!");
      ch.check("Hello client!");

      s1.shutdown();
      Thread.sleep(20);

      ch.send("Hello server!");
      sh.check("Hello server!");

      sh.send("Hello client!");
      ch.check("Hello client!");

      s2.shutdown();
      Thread.sleep(20);

      ch.send("Hello server!");
      sh.check("");

      sh.send("Hello client!");
      ch.check("");

      c.shutdown();
   }

   public static class MockHandler implements SocketHandler {
      private StringBuilder m_sb = new StringBuilder(1024);

      private Channel m_channel;

      private BlockingQueue<String> m_queue = new LinkedBlockingQueue<String>();

      @Override
      public Pair<Channel, ChannelBuffer> getNextMessage() {
         String message = m_queue.poll();

         if (message != null) {
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            byte[] data = message.getBytes();

            buffer.writeInt(data.length);
            buffer.writeBytes(data);
            return new Pair<Channel, ChannelBuffer>(m_channel, buffer);
         }

         return null;
      }

      @Override
      public void onConnected(Channel channel) throws Exception {
         m_channel = channel;
      }

      @Override
      public void onDisconnected(Channel channel) throws Exception {
      }

      @Override
      public void onException(Channel channel, Throwable cause) throws Exception {
         channel.close();
      }

      @Override
      public void onMessage(Channel channel, ChannelBuffer buffer) throws Exception {
         int len = buffer.readInt();
         byte[] data = new byte[len];

         buffer.readBytes(data);

         m_sb.append(new String(data));
      }

      public void send(String message) {
         m_queue.offer(message);
      }

      public void check(String expected) {
         long deadline = System.currentTimeMillis() + 100;

         try {
            while (deadline > System.currentTimeMillis() && m_sb.length() == 0) {
               TimeUnit.MILLISECONDS.sleep(10);
            }
         } catch (InterruptedException e) {
            // ignore it
         }

         String actual = m_sb.toString();

         m_sb.setLength(0);
         Assert.assertEquals(expected, actual);
      }
   }
}
