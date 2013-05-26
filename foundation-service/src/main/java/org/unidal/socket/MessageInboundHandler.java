package org.unidal.socket;

public interface MessageInboundHandler<T extends Message> {
   public void handle(T message);
}
