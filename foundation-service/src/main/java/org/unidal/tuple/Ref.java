package org.unidal.tuple;

/**
 * Tuple to hold one element.
 */
public class Ref<S> implements Tuple {
   private volatile S m_value;

   public Ref() {
   }

   public Ref(S value) {
      m_value = value;
   }

   public static <T> Ref<T> from(T value) {
      return new Ref<T>(value);
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof Ref) {
         Ref<Object> o = (Ref<Object>) obj;

         if (m_value == null) {
            return o.m_value == null;
         } else {
            return m_value.equals(o.m_value);
         }
      }

      return false;
   }

   @Override
   @SuppressWarnings({ "unchecked" })
   public <T> T get(int index) {
      switch (index) {
      case 0:
         return (T) m_value;
      default:
         throw new IndexOutOfBoundsException(String.format("Index from 0 to %s, but was %s!", size(), index));
      }
   }

   public S getValue() {
      return m_value;
   }

   @Override
   public int hashCode() {
      return m_value == null ? 0 : m_value.hashCode();
   }

   public void setValue(S value) {
      m_value = value;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public String toString() {
      return String.format("Ref[value=%s]", m_value);
   }
}
