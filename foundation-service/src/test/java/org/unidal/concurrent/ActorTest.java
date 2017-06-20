package org.unidal.concurrent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.unidal.concurrent.AbstractActor;
import org.unidal.concurrent.AbstractActorContext;
import org.unidal.concurrent.Stage;
import org.unidal.concurrent.StageManager;
import org.unidal.lookup.ComponentTestCase;

public class ActorTest extends ComponentTestCase {
   @Test
   public void test() throws InterruptedException, IOException {
      System.setProperty("devMode", "true");

      StageManager manager = lookup(StageManager.class);
      Stage<Integer> stage = manager.getStage("first");

      stage.add(new Alice(3, 0));
      stage.add(new Alice(3, 1));
      stage.add(new Alice(3, 2));
      stage.add(new Alice(2, 0));
      stage.add(new Alice(2, 1));
      stage.add(new Alice(1, 0));

      for (int i = 0; i < 1000; i++) {
         for (int j = 0; j < 10000; j++) {
            stage.distribute(i * 10000 + j);
         }

         TimeUnit.MILLISECONDS.sleep(10);
      }

      TimeUnit.MILLISECONDS.sleep(5000);

      stage.shutdown();
   }

   private static class Alice extends AbstractActor<Integer, AliceContext> {
      public Alice(int size, int index) {
         super(new AliceContext(size, index));
      }

      @Override
      protected void run(AliceContext ctx) throws InterruptedException {
         List<Integer> events = ctx.nextBatch();
         int len = events.size();
         int count = ctx.count().addAndGet(len);

         if (count % 100000 < len) {
            System.out.println("count: " + count);
         }

         if (count % 10 == 0) {
            TimeUnit.MILLISECONDS.sleep(1);
         }
      }
   }

   private static class AliceContext extends AbstractActorContext<Integer> {
      private int m_size;

      private int m_index;

      private AtomicInteger m_count = new AtomicInteger();

      public AliceContext(int size, int index) {
         m_size = size;
         m_index = index;
      }

      @Override
      public boolean addLast(Integer event) throws InterruptedException {
         if (event.intValue() % m_size == m_index) {
            return super.addLast(event);
         } else {
            return true;
         }
      }

      public AtomicInteger count() {
         return m_count;
      }
   }
}
