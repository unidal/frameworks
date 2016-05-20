package org.unidal.net;

public interface TransportRepository {
   public Object get();

   public boolean isEmpty();

   public boolean put(Object message);
}
