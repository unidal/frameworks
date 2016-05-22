package org.unidal.net.transport;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.net.ServerTransport;

@Named(type = ServerTransport.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultServerTransport implements ServerTransport {
   @Inject
   private ServerTransportHandler m_handler;

   private ServerTransportDescriptor m_desc = new ServerTransportDescriptor();

   @Override
   public ServerTransport bind(int port) {
      m_desc.setLocalAddress(new InetSocketAddress(port));
      return this;
   }

   @Override
   public ServerTransport handler(String name, ChannelHandler handler) {
      m_desc.addHandler(name, handler);
      return this;
   }

   @Override
   public ServerTransport name(String name) {
      m_desc.setName(name);
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ServerTransport option(ChannelOption<T> option, T value) {
      m_desc.getOptions().put((ChannelOption<Object>) option, value);
      return this;
   }

   @Override
   public ServerTransport start() {
      m_desc.validate();
      m_handler.setDescriptor(m_desc);

      Threads.forGroup(m_desc.getName()).start(m_handler);

      try {
         m_handler.awaitWarmup();
      } catch (InterruptedException e) {
         // ignore it
      }

      return this;
   }

   @Override
   public void stop(int timeout, TimeUnit unit) throws InterruptedException {
      m_handler.shutdown();
      m_handler.awaitTermination(timeout, unit);
   }

   @Override
   public ServerTransport withBossThreads(int bossThreads) {
      m_desc.setBossThreads(bossThreads);
      return this;
   }

   @Override
   public ServerTransport withWorkerThreads(int workerThreads) {
      m_desc.setWorkerThreads(workerThreads);
      return this;
   }

   @Override
   public boolean write(Object message) {
      return m_handler.write(message);
   }
}
