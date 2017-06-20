package org.unidal.concurrent;

public interface Actor<E, C extends ActorContext<E>> {
   public ActorState checkState();

   public C getContext();

   public void play() throws InterruptedException;
}
