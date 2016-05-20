package org.unidal.net.transport;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.unidal.lookup.annotation.Named;
import org.unidal.net.TransportRepository;

@Named(type = TransportRepository.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultTransportRepository implements TransportRepository {
   private BlockingQueue<Object> m_queue = new ArrayBlockingQueue<Object>(1000);

   @Override
   public Object get() {
      return m_queue.poll();
   }

   @Override
   public boolean isEmpty() {
      return m_queue.isEmpty();
   }

   @Override
   public boolean put(Object message) {
      return m_queue.offer(message);
   }
}
