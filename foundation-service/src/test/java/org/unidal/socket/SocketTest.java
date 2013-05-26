package org.unidal.socket;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.socket.tcp.TcpSocketClient;
import org.unidal.socket.tcp.TcpSocketServer;
import org.unidal.socket.udp.UdpSocket;

public class SocketTest extends ComponentTestCase {
   @Before
   public void before() {
      ThreadRenamingRunnable.setThreadNameDeterminer(ThreadNameDeterminer.CURRENT);
   }
   
   @Test
   public void testTcp() throws SocketException, InterruptedException {
      StringBuilder sb = new StringBuilder(4096);
      MessageCodec<MockMessage> codec = new MockMessageCodec();
      TcpSocketServer server = new TcpSocketServer();

      server.setName("MockServer");
      server.onMessage(new MockMessageInboundHandler(sb));
      server.setCodec(codec);
      server.listenOn(new InetSocketAddress(3663));

      TcpSocketClient client = new TcpSocketClient();

      client.setName("MockClient");
      client.setCodec(codec);
      client.connectTo(new InetSocketAddress("localhost", 3663));

      for (int i = 0; i < 1000; i++) {
         client.send(new MockMessage(i));
      }

      TimeUnit.MILLISECONDS.sleep(1000);
      Assert.assertEquals(3890, sb.length());
   }

   @Test
   public void testUdp() throws InterruptedException {
      StringBuilder sb = new StringBuilder(4096);
      UdpSocket udp = new UdpSocket();

      udp.setName("MockUdp");
      udp.setCodec(new MockMessageCodec());
      udp.onMessage(new MockMessageInboundHandler(sb));
      udp.listenOn(new InetSocketAddress("224.0.0.1", 3663));

      for (int i = 0; i < 1000; i++) {
         udp.send(new MockMessage(i));
      }

      TimeUnit.MILLISECONDS.sleep(1000);
      Assert.assertEquals(3890, sb.length());
   }

   static class MockMessage implements Message {
      private int m_id;

      public MockMessage(int id) {
         m_id = id;
      }

      public int getId() {
         return m_id;
      }
   }

   static class MockMessageCodec implements MessageCodec<MockMessage> {
      @Override
      public MockMessage decode(ChannelBuffer buffer) {
         buffer.readInt(); // get rid of the place-holder

         int id = buffer.readInt();

         return new MockMessage(id);
      }

      @Override
      public ChannelBuffer encode(MockMessage message) {
         ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

         buffer.writeInt(0); // the place-holder
         buffer.writeInt(message.getId());
         return buffer;
      }
   }

   static class MockMessageInboundHandler implements MessageInboundHandler<MockMessage> {
      private StringBuilder m_sb;

      public MockMessageInboundHandler(StringBuilder sb) {
         m_sb = sb;
      }

      @Override
      public void handle(MockMessage message) {
         m_sb.append(message.getId()).append('|');
      }
   }
}
