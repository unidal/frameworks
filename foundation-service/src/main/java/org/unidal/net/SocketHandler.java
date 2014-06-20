package org.unidal.net;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.unidal.tuple.Pair;

public interface SocketHandler {
	public Pair<Channel, ChannelBuffer> getNextMessage();

	public void onConnected(Channel channel) throws Exception;

	public void onDisconnected(Channel channel) throws Exception;

	public void onException(Channel channel, Throwable cause) throws Exception;

	public void onMessage(Channel channel, ChannelBuffer buffer) throws Exception;
}
