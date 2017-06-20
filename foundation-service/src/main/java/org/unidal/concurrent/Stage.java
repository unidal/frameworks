package org.unidal.concurrent;

import org.unidal.helper.Threads.Task;

public interface Stage<E> extends Task {
   public void add(Actor<E, ?> actor);

   public boolean distribute(E event) throws InterruptedException;

   public String getId();

   public StageStatus getStatus();

   public int show() throws InterruptedException;
}
