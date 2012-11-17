package org.unidal.dal.jdbc;

public class Ref<T> {
   private T m_value;

   public T get() {
      return m_value;
   }

   public void set(T value) {
      m_value = value;
   }
}
