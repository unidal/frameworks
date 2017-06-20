package org.unidal.concurrent.internals;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;

@Named(type = ThreadPoolManager.class)
public class DefaultThreadPoolManager extends ContainerHolder implements ThreadPoolManager {
   private Map<String, ThreadPool> m_pools = new HashMap<String, ThreadPool>();

   @Override
   public synchronized ThreadPool getThreadPool(String id) {
      ThreadPool pool = m_pools.get(id);

      if (pool == null) {
         pool = lookup(ThreadPool.class);

         m_pools.put(id, pool);
      }

      return pool;
   }

   @Override
   public void shutdown() {
      for (ThreadPool pool : m_pools.values()) {
         pool.shutdown();
      }
   }
}
