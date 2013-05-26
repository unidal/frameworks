package org.unidal.socket;

import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;

public interface SocketListener {
   public void onConnectionFailure(InetSocketAddress address, Throwable cause);

   public void onConnectionSuccess(InetSocketAddress address);

   public void onDisconnected(InetSocketAddress remoteAddress);

   public void onReceived(ChannelBuffer buffer);

   public void onReceivingFailure(ChannelBuffer buffer, Throwable cause);

   public void onReceivingOverflowed(ChannelBuffer buffer);

   public void onSendingFailure(Message message, Throwable cause);

   public void onSendingOverflowed(Message message);

   public void onSent(ChannelBuffer buffer);

   public void onWriteBufferFull(int attempts);
}
