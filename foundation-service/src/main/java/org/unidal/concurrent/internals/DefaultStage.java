package org.unidal.concurrent.internals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.unidal.concurrent.Actor;
import org.unidal.concurrent.Stage;
import org.unidal.concurrent.StageConfiguration;
import org.unidal.concurrent.StageStatus;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = Stage.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultStage<E> implements Stage<E> {
   @Inject
   private ThreadPoolManager m_poolManager;

   @Inject
   private ActorManager<E> m_actorManager;

   private String m_id;

   private ThreadPool m_pool;

   private StageConfiguration m_config = new DefaultStageConfiguration();

   private AtomicBoolean m_enabled = new AtomicBoolean(true);

   private CountDownLatch m_latch = new CountDownLatch(1);

   private DefaultStageStatus m_status;

   @Override
   public void add(Actor<E, ?> actor) {
      m_actorManager.addActor(actor);
   }

   @Override
   public String getId() {
      return m_id;
   }

   @Override
   public String getName() {
      return String.format("%s[%s]", getClass().getSimpleName(), m_id);
   }

   @Override
   public StageStatus getStatus() {
      return m_status;
   }

   @Override
   public boolean distribute(E event) throws InterruptedException {
      if (m_enabled.get()) {
         m_actorManager.distribute(event, m_enabled);

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void run() {
      try {
         m_pool = m_poolManager.getThreadPool(m_id);
         m_pool.start(this, m_config);

         int index = 0;

         while (m_enabled.get()) {
            int interval = 1000; // 1 second
            long deadline = System.currentTimeMillis() + interval;
            DefaultStageStatus status = new DefaultStageStatus(interval);

            m_actorManager.report(status);
            m_pool.report(status);
            m_pool.adjust(status, m_status);
            m_status = status;

            if (index++ % 1 == 0) { // TODO test
               System.out.println(status);
            }

            sleepUntil(deadline);
         }
      } catch (InterruptedException e) {
         // ignore
      } catch (Throwable e) {
         e.printStackTrace();
      } finally {
         m_latch.countDown();
      }
   }

   public void setId(String id) {
      m_id = id;
   }

   @Override
   public int show() throws InterruptedException {
      Actor<E, ?> actor = m_actorManager.getNextActor();

      if (actor != null) {
         actor.play();
         return actor.getContext().getProcessed();
      } else {
         return 0;
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

      m_pool.shutdown();
   }

   private void sleepUntil(long deadline) throws InterruptedException {
      while (true) {
         long now = System.currentTimeMillis();

         if (now < deadline) {
            TimeUnit.MILLISECONDS.sleep(deadline - now);
         } else {
            break;
         }
      }
   }
}
