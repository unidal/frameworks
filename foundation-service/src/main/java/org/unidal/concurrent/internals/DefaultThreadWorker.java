package org.unidal.concurrent.internals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.unidal.concurrent.Stage;

public class DefaultThreadWorker implements ThreadWorker {
   private Stage<?> m_stage;

   private int m_index;

   private AtomicLong m_count = new AtomicLong();

   private AtomicLong m_costInNanos = new AtomicLong();

   private AtomicBoolean m_enabled = new AtomicBoolean(true);

   private CountDownLatch m_latch = new CountDownLatch(1);

   public DefaultThreadWorker(Stage<?> stage, int index) {
      m_stage = stage;
      m_index = index;
   }

   @Override
   public int getAndResetCostInMillis() {
      return (int) (m_costInNanos.getAndSet(0) / 1000000L);
   }

   @Override
   public long getAndResetCount() {
      return m_count.getAndSet(0);
   }

   @Override
   public String getName() {
      return String.format("ThreadWorker[%s]-%s", m_stage.getId(), m_index);
   }

   @Override
   public void run() {
      try {
         while (m_enabled.get()) {
            long start = System.nanoTime();
            int count = m_stage.show();

            if (count > 0) {
               m_costInNanos.addAndGet(System.nanoTime() - start);
               m_count.addAndGet(count);
            } else {
               TimeUnit.MILLISECONDS.sleep(1);
            }
         }
      } catch (InterruptedException e) {
         // ignore
      } catch (Throwable e) {
         e.printStackTrace();
      } finally {
         m_latch.countDown();
      }
   }

   @Override
   public void shutdown() {
      m_enabled.set(false);

      try {
         m_latch.await();
      } catch (InterruptedException e) {
         // ignore it
      }
   }
}
