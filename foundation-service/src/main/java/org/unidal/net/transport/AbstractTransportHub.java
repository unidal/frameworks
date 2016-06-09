package org.unidal.net.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class AbstractTransportHub<T> implements TransportHub {
   protected abstract T decode(ByteBuf buf);

   protected abstract void encode(ByteBuf buf, T message);

   @Override
   public boolean fill(ByteBuf buf) {
      T message = nextMessage();

      if (message != null) {
         buf.writeByte(0xCA);
         buf.writeByte(0xFE);
         buf.writeInt(0);

         encode(buf, message);

         buf.setInt(2, buf.readableBytes() - 6);
         return true;
      }

      return false;
   }

   protected abstract void handle(T message, Channel channel);

   protected abstract T nextMessage();

   @Override
   public void onMessage(ByteBuf buf, Channel channel) {
      T message = decode(buf);

      handle(message, channel);
   }
}
