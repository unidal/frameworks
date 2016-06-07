package org.unidal.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public interface ClientTransport {
   public ClientTransport connect(InetSocketAddress... addresses);

   public ClientTransport connect(SocketAddressProvider provider);

   public ClientTransport connect(String host, int port);

   public ClientTransport handler(String name, ChannelHandler handler);

   public ClientTransport name(String name);

   public <T> ClientTransport option(ChannelOption<T> option, T value);

   public ClientTransport start();

   public void stop(int timeout, TimeUnit unit) throws InterruptedException;

   public ClientTransport withThreads(int threads);

   public boolean write(Object message);
}
