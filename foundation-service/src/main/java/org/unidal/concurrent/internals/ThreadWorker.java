package org.unidal.concurrent.internals;

import org.unidal.helper.Threads.Task;

public interface ThreadWorker extends Task {
   public long getAndResetCount();

   public int getAndResetCostInMillis();
}