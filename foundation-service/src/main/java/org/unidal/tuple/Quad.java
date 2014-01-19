package org.unidal.tuple;

/**
 * Tuple to hold four elements: east, south, west and north.
 * 
 * @param <E>
 *           east
 * @param <S>
 *           south
 * @param <W>
 *           west
 * @param <N>
 *           north
 */
public class Quad<E, S, W, N> implements Tuple {
   private volatile E m_east;

   private volatile S m_south;

   private volatile W m_west;

   private volatile N m_north;

   public Quad() {
   }

   public Quad(E east, S south, W west, N north) {
      m_east = east;
      m_south = south;
      m_west = west;
      m_north = north;
   }

   public static <E, S, W, N> Quad<E, S, W, N> from(E east, S south, W west, N north) {
      return new Quad<E, S, W, N>(east, south, west, north);
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof Quad) {
         Quad<Object, Object, Object, Object> o = (Quad<Object, Object, Object, Object>) obj;

         if (m_east == null) {
            if (o.m_east != null) {
               return false;
            }
         } else if (!m_east.equals(o.m_east)) {
            return false;
         }

         if (m_south == null) {
            if (o.m_south != null) {
               return false;
            }
         } else if (!m_south.equals(o.m_south)) {
            return false;
         }

         if (m_west == null) {
            if (o.m_west != null) {
               return false;
            }
         } else if (!m_west.equals(o.m_west)) {
            return false;
         }

         if (m_north == null) {
            if (o.m_north != null) {
               return false;
            }
         } else if (!m_north.equals(o.m_north)) {
            return false;
         }

         return true;
      }

      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T get(int index) {
      switch (index) {
      case 0:
         return (T) m_east;
      case 1:
         return (T) m_south;
      case 2:
         return (T) m_west;
      case 3:
         return (T) m_north;
      default:
         throw new IndexOutOfBoundsException(String.format("Index from 0 to %s, but was %s!", size(), index));
      }
   }

   public E getEast() {
      return m_east;
   }

   public N getNorth() {
      return m_north;
   }

   public S getSouth() {
      return m_south;
   }

   public W getWest() {
      return m_west;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_east == null ? 0 : m_east.hashCode());
      hash = hash * 31 + (m_south == null ? 0 : m_south.hashCode());
      hash = hash * 31 + (m_west == null ? 0 : m_west.hashCode());
      hash = hash * 31 + (m_north == null ? 0 : m_north.hashCode());

      return hash;
   }

   public void setEast(E east) {
      m_east = east;
   }

   public void setNorth(N north) {
      m_north = north;
   }

   public void setSouth(S south) {
      m_south = south;
   }

   public void setWest(W west) {
      m_west = west;
   }

   @Override
   public int size() {
      return 4;
   }

   @Override
   public String toString() {
      return String.format("Quad[east=%s, south=%s, west=%s, north=%s]", m_east, m_south, m_west, m_north);
   }
}
