package org.unidal.socket;

import java.net.InetSocketAddress;

public interface SocketClient {
   public void connectTo(InetSocketAddress... addresses);

   public void send(Message message);

   public <T extends Message> void send(T message, MessageOutboundHandler<T> callback, Object context);
}
