package org.unidal.net.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public abstract class FrameMessageDecoder<T> extends ByteToMessageDecoder {
   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
      if (buf.readableBytes() < 6) {
         return;
      }

      int index = buf.readerIndex();
      short b1 = buf.getUnsignedByte(index);
      short b2 = buf.getUnsignedByte(index + 1);
      int length = buf.getInt(index + 2);

      if (b1 != 0xCA || b2 != 0xFE) { // not 0xCAFE
         throw new DecoderException("Bad header bytes!");
      } else if (buf.readableBytes() >= length + 6) {
         ByteBuf frame = buf.slice(index + 6, length);

         buf.readerIndex(index + 6 + length);
         frame.retain();

         try {
            Object msg = frameToMessage(ctx, frame);

            if (msg != null) {
               out.add(msg);
            }
         } finally {
            frame.release();
         }
      }
   }

   protected abstract T frameToMessage(ChannelHandlerContext ctx, ByteBuf frame);
}
