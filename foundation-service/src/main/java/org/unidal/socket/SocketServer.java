package org.unidal.socket;

import java.net.InetSocketAddress;

public interface SocketServer {
   public void listenOn(InetSocketAddress address);

   public <T extends Message> void onMessage(MessageInboundHandler<T> handler);
}
