package org.unidal.net.transport.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.logger.LoggerFactory;

public class ServerStateHandler extends ChannelInboundHandlerAdapter implements Cloneable {
   private Logger m_logger = LoggerFactory.getLogger(getClass());

   private String m_name;

   public ServerStateHandler(String name) {
      m_name = name;
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();
      InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();

      m_logger.info(String.format("%s client at %s:%s joined", m_name, address.getHostName(), address.getPort()));
      super.channelActive(ctx);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();
      InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();

      m_logger.info(String.format("%s client at %s:%s left", m_name, address.getHostName(), address.getPort()));
      super.channelInactive(ctx);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      ctx.channel().close();

      m_logger.error(cause.getMessage(), cause);
   }
}
