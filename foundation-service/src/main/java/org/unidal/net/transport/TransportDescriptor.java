package org.unidal.net.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

import java.util.Map;

public interface TransportDescriptor {
   public Class<? extends Channel> getChannelClass();

   public ChannelInitializer<? extends Channel> getInitializer();

   public String getName();

   public Map<ChannelOption<Object>, Object> getOptions();

   public EventLoopGroup getGroup();

   public void validate();
}
