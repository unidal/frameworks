package org.unidal.socket;

import org.jboss.netty.buffer.ChannelBuffer;

public interface MessageCodec<T extends Message> {
   public ChannelBuffer encode(T message);

   public T decode(ChannelBuffer buffer);
}
