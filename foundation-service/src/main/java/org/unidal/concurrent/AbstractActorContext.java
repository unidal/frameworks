package org.unidal.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractActorContext<E> implements ActorContext<E> {
   private BlockingQueue<E> m_queue;

   private AtomicLong m_added = new AtomicLong();

   private AtomicLong m_overflowed = new AtomicLong();

   private AtomicInteger m_processed = new AtomicInteger();

   private AtomicLong m_lastAccess;

   public AbstractActorContext() {
      m_queue = new ArrayBlockingQueue<E>(getInitialQueueSize());
      m_lastAccess = new AtomicLong(System.currentTimeMillis());
   }

   @Override
   public boolean addLast(E event) throws InterruptedException {
      if (isBlocking()) { // retry at outer loop
         if (m_queue.offer(event, 5, TimeUnit.MILLISECONDS)) {
            m_added.incrementAndGet();
            return true;
         } else {
            return false;
         }
      } else { // no retry for overflowed event
         if (m_queue.offer(event)) {
            m_added.incrementAndGet();
         } else {
            m_overflowed.incrementAndGet();
         }

         return true;
      }
   }

   @Override
   public int available() {
      return m_queue.size();
   }

   protected long getAdded() {
      return m_added.get();
   }

   protected int getBatchSize() {
      return 100;
   }

   protected int getInitialQueueSize() {
      return 500000;
   }

   protected long getOverflowed() {
      return m_overflowed.get();
   }

   @Override
   public int getProcessed() {
      return m_processed.getAndSet(0);
   }

   @Override
   public boolean isBatchReady() {
      int available = m_queue.size();

      return available >= getBatchSize() || available > 0 && m_lastAccess.get() + 10 < System.currentTimeMillis();
   }

   protected boolean isBlocking() {
      return true;
   }

   public E next() throws InterruptedException {
      return m_queue.poll();
   }

   @Override
   public List<E> nextBatch() {
      int maxSize = getBatchSize();
      List<E> m_batch = new ArrayList<E>(maxSize);

      m_queue.drainTo(m_batch, maxSize);
      m_processed.addAndGet(m_batch.size());
      m_lastAccess.set(System.currentTimeMillis());

      return m_batch;
   }
}
