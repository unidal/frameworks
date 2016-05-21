package org.unidal.net.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

import java.util.Map;

public interface TransportDescriptor {
   public Class<? extends Channel> getChannelClass();

   public EventLoopGroup getGroup();

   public Map<String, ChannelHandler> getHandlers();

   public String getName();

   public Map<ChannelOption<Object>, Object> getOptions();

   public void validate();
}
