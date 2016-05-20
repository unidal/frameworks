package org.unidal.net;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;

public interface ClientTransport {
   public ClientTransport connect(String host, int port);

   public ClientTransport name(String name);

   public <T> ClientTransport option(ChannelOption<T> option, T value);

   public ClientTransport start(ChannelInitializer<Channel> initializer);

   public void stop(int timeout, TimeUnit unit) throws InterruptedException;

   public ClientTransport withThreads(int threads);

   public boolean write(Object message);
}
