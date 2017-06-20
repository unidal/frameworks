package org.unidal.concurrent.internals;

public interface ThreadPoolManager {
   public ThreadPool getThreadPool(String id);

   public void shutdown();
}
