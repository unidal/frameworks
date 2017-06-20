package org.unidal.concurrent;

import java.util.List;

public interface ActorContext<E> {
   public boolean addLast(E event) throws InterruptedException;

   public int available();

   public int getProcessed();

   public boolean isBatchReady();

   public E next() throws InterruptedException;

   public List<E> nextBatch();
}
