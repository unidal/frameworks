package org.unidal.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.unidal.net.Sockets.SocketClient;
import org.unidal.net.Sockets.SocketServer;

public class SocketsTest {
   @Test
   public void testApi() throws InterruptedException {
      MockMessageDelegate delegate = new MockMessageDelegate();
      SocketServer server = Sockets.forServer().listenOn(1234).threads("TestServer", 3).start(delegate);
      SocketClient client = Sockets.forClient().connectTo(1234, "localhost").threads("TestClient", 2).start(delegate);
      int count = 100;

      for (int i = 0; i < count; i++) {
         delegate.enqueue("Mocked");
      }

      TimeUnit.MILLISECONDS.sleep(100);

      client.shutdown();
      server.shutdown();

      Assert.assertEquals("Not all messages are sent or received.", count, delegate.getReceived());
   }

   public class MockMessageDelegate implements MessageDelegate {
      private BlockingQueue<ChannelBuffer> m_queue = new LinkedBlockingQueue<ChannelBuffer>(100);

      private int m_received;

      public boolean enqueue(String message) {
         ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
         byte[] bytes = message.getBytes();

         buffer.writeInt(bytes.length);
         buffer.writeBytes(bytes);
         return m_queue.offer(buffer);
      }

      public int getReceived() {
         return m_received;
      }

      @Override
      public ChannelBuffer nextMessage(long timeout, TimeUnit unit) throws InterruptedException {
         return m_queue.poll(timeout, unit);
      }

      @Override
      public void onMessageReceived(ChannelBuffer buffer) {
         m_received++;
      }
   }
}
