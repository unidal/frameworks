package org.unidal.net.transport;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class ServerTransportDescriptor implements TransportDescriptor {
   private InetSocketAddress m_localAddress;

   private String m_name;

   private int m_bossThreads = 1;

   private int m_workerThreads = 3;

   private Map<ChannelOption<Object>, Object> m_options = new HashMap<ChannelOption<Object>, Object>();

   private Map<String, ChannelHandler> m_handlers = new LinkedHashMap<String, ChannelHandler>();

   private TransportHub m_hub;

   public void addHandler(String name, ChannelHandler handler) {
      m_handlers.put(name, handler);
   }

   public EventLoopGroup getBossGroup() {
      ThreadFactory factory = new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread(r);

            t.setName(m_name + "-BossGroup");
            t.setDaemon(true);
            return t;
         }
      };

      return new NioEventLoopGroup(m_bossThreads, factory);
   }

   public ByteBufAllocator getByteBufAllocator() {
      Object allocator = m_options.get(ChannelOption.ALLOCATOR);

      if (allocator == null) {
         return PooledByteBufAllocator.DEFAULT;
      } else {
         return (ByteBufAllocator) allocator;
      }
   }

   @Override
   public Class<? extends ServerChannel> getChannelClass() {
      return NioServerSocketChannel.class;
   }

   @Override
   public EventLoopGroup getGroup() {
      ThreadFactory factory = new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread(r);

            t.setName(m_name + "-WorkerGroup");
            t.setDaemon(true);
            return t;
         }
      };

      return new NioEventLoopGroup(m_workerThreads, factory);
   }

   @Override
   public Map<String, ChannelHandler> getHandlers() {
      return m_handlers;
   }

   @Override
   public TransportHub getHub() {
      return m_hub;
   }

   public InetSocketAddress getLocalAddress() {
      return m_localAddress;
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public Map<ChannelOption<Object>, Object> getOptions() {
      return m_options;
   }

   public void setBossThreads(int bossThreads) {
      m_bossThreads = bossThreads;
   }

   public void setHub(TransportHub hub) {
      m_hub = hub;
   }

   public void setLocalAddress(InetSocketAddress localAddress) {
      m_localAddress = localAddress;
   }

   public void setName(String name) {
      m_name = name;
   }

   public void setWorkerThreads(int workerThreads) {
      m_workerThreads = workerThreads;
   }

   @Override
   public void validate() {
   }
}
