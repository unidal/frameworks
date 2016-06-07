package org.unidal.net.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import org.unidal.net.SocketAddressProvider;

public class ClientTransportDescriptor implements TransportDescriptor {
   private String m_name;

   private SocketAddressProvider m_addressProvider;

   private int m_threads = 3;

   private Map<ChannelOption<Object>, Object> m_options = new HashMap<ChannelOption<Object>, Object>();

   private Map<String, ChannelHandler> m_handlers = new LinkedHashMap<String, ChannelHandler>();

   public void addHandler(String name, ChannelHandler handler) {
      m_handlers.put(name, handler);
   }

   @Override
   public Class<? extends Channel> getChannelClass() {
      return NioSocketChannel.class;
   }

   @Override
   public EventLoopGroup getGroup() {
      ThreadFactory factory = new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread(r);

            t.setName(m_name + "-NioEventLoopGroup");
            t.setDaemon(true);
            return t;
         }
      };

      return new NioEventLoopGroup(m_threads, factory);
   }

   @Override
   public Map<String, ChannelHandler> getHandlers() {
      return m_handlers;
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public Map<ChannelOption<Object>, Object> getOptions() {
      return m_options;
   }

   public List<InetSocketAddress> getRemoteAddresses() {
      return m_addressProvider.getAddresses();
   }

   public void setAddressProvider(SocketAddressProvider addressProvider) {
      m_addressProvider = addressProvider;
   }

   public void setName(String name) {
      m_name = name;
   }

   public void setThreads(int threads) {
      m_threads = threads;
   }

   @Override
   public void validate() {

   }
}
