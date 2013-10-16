package org.unidal.tuple;

public interface Tuple {
   public <T> T get(int index);

   public int size();
}
