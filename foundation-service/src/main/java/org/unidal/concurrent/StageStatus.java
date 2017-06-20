package org.unidal.concurrent;

public interface StageStatus {
   public int getActors();

   public int[] getAvailable();

   public int getIntervalInMillis();

   public int[] getProcessedCosts();

   public long[] getProcessedCounts();

   public int getThreads();
}
