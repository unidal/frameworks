package org.unidal.concurrent.internals;

import java.util.ArrayList;
import java.util.List;

import org.unidal.concurrent.Stage;
import org.unidal.concurrent.StageConfiguration;
import org.unidal.concurrent.StageStatus;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = ThreadPool.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultThreadPool implements ThreadPool, LogEnabled {
   private static final int WINDOW_SIZE = 1;

   private Stage<?> m_stage;

   private StageConfiguration m_config;

   private List<ThreadWorker> m_workers = new ArrayList<ThreadWorker>();

   private List<Integer> m_flags = new ArrayList<Integer>();

   private Logger m_logger;

   @Override
   public void adjust(StageStatus current, StageStatus last) {
      if (current instanceof DefaultStageStatus) {
         DefaultStageStatus status = (DefaultStageStatus) current;
         int flag = filter(status.checkThroughput(last));
         int threads = m_workers.size();

         if (flag > 0 && threads < Math.min(status.getActors(), m_config.getThreadMaxCount())) {
            createNewWorker();
         } else if (flag < 0 && threads > m_config.getThreadMinCount()) {
            removeLastWorker();
         } else { // flag == 0
            // stay unchanged
         }
      }
   }

   private void createNewWorker() {
      int index = m_workers.size();
      ThreadWorker worker = new DefaultThreadWorker(m_stage, index);

      m_logger.info(String.format("Create worker[%s] of stage(%s)", index, m_stage.getId()));
      Threads.forGroup("Cat").start(worker);
      m_workers.add(worker);
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   private int filter(int flag) {
      if (m_flags.size() >= WINDOW_SIZE) {
         m_flags.remove(0);
      }

      m_flags.add(flag);

      int total = 0;

      for (Integer f : m_flags) {
         total += f.intValue();
      }

      if (total == WINDOW_SIZE) {
         return 1;
      } else if (total == -WINDOW_SIZE) {
         return -1;
      } else {
         return 0;
      }
   }

   private void removeLastWorker() {
      int index = m_workers.size() - 1;
      ThreadWorker worker = m_workers.remove(index);

      m_logger.info(String.format("Remove worker[%s] of stage(%s)", index, m_stage.getId()));
      worker.shutdown();
   }

   @Override
   public void report(StageStatus status) {
      int len = m_workers.size();
      long[] counts = new long[len];
      int[] costs = new int[len];

      for (int i = 0; i < len; i++) {
         ThreadWorker worker = m_workers.get(i);
         long count = worker.getAndResetCount();
         int cost = worker.getAndResetCostInMillis();

         counts[i] = count;
         costs[i] = cost;
      }

      if (status instanceof DefaultStageStatus) {
         ((DefaultStageStatus) status).setProcessed(costs, counts);
      }
   }

   @Override
   public void shutdown() {
      for (ThreadWorker worker : m_workers) {
         worker.shutdown();
      }
   }

   @Override
   public void start(Stage<?> stage, StageConfiguration config) {
      m_stage = stage;
      m_config = config;

      for (int i = 0; i < config.getThreadMinCount(); i++) {
         createNewWorker();
      }
   }
}
