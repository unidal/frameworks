package org.unidal.net.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class FrameMessageEncoder<T> extends MessageToByteEncoder<T> {
   @Override
   protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf buf) throws Exception {
      buf.writeByte(0xCA);
      buf.writeByte(0xFE);
      buf.writeInt(0);

      messageToFrame(ctx, msg, buf);

      buf.setInt(2, buf.readableBytes() - 6);
   }

   protected abstract void messageToFrame(ChannelHandlerContext ctx, T msg, ByteBuf frame);
}
