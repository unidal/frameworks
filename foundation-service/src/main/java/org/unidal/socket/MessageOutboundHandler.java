package org.unidal.socket;

public interface MessageOutboundHandler<T extends Message> {
   public void onError(T message, Throwable e, Object context);

   public void onSendingOverflowed(T message, Object context);

   public void onSent(T message, Object context);
}
