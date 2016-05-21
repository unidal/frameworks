package org.unidal.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

import java.util.concurrent.TimeUnit;

public interface ServerTransport {
   public ServerTransport bind(int port);

   public ServerTransport handler(String name, ChannelHandler handler);

   public ServerTransport name(String name);

   public <T> ServerTransport option(ChannelOption<T> option, T value);

   public ServerTransport start();

   public void stop(int timeout, TimeUnit unit) throws InterruptedException;

   public ServerTransport withBossThreads(int bossThreads);

   public ServerTransport withWorkerThreads(int workerThreads);

   public boolean write(Object message);
}
