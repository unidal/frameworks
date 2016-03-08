package org.unidal.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Sorters {
   public static ListSorter forList() {
      return ListSorter.ASC;
   }

   public static MapSorter forMap() {
      return MapSorter.ASC;
   }

   public static SetSorter forSet() {
      return SetSorter.ASC;
   }

   public enum ListSorter {
      ASC(true),

      DESC(false);

      private boolean m_ascend;

      private ListSorter(boolean ascend) {
         m_ascend = ascend;
      }

      public ListSorter ascend() {
         return ASC;
      }

      public ListSorter descend() {
         return DESC;
      }

      public <T> List<T> sort(List<T> list, final Comparator<T> comparator) {
         Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
               int result = comparator.compare(o1, o2);

               return m_ascend ? result : -result;
            }
         });

         return list;
      }
   }

   public enum MapSorter {
      ASC(true),

      DESC(false);

      private boolean m_ascend;

      private MapSorter(boolean ascend) {
         m_ascend = ascend;
      }

      public MapSorter ascend() {
         return ASC;
      }

      public MapSorter descend() {
         return DESC;
      }

      public <S, T> Map<S, T> sort(Map<S, T> map, final Comparator<T> comparator) {
         List<Map.Entry<S, T>> entries = new ArrayList<Map.Entry<S, T>>(map.entrySet());

         Collections.sort(entries, new Comparator<Map.Entry<S, T>>() {
            @Override
            public int compare(Entry<S, T> o1, Entry<S, T> o2) {
               int result = comparator.compare(o1.getValue(), o2.getValue());

               return m_ascend ? result : -result;
            }
         });

         if (map instanceof LinkedHashMap) {
            map.clear();

            for (Map.Entry<S, T> entry : entries) {
               map.put(entry.getKey(), entry.getValue());
            }

            return map;
         } else {
            LinkedHashMap<S, T> result = new LinkedHashMap<S, T>(entries.size() * 4 / 3 + 1);

            for (Map.Entry<S, T> entry : entries) {
               result.put(entry.getKey(), entry.getValue());
            }

            return result;
         }
      }
   }

   public enum SetSorter {
      ASC(true),

      DESC(false);

      private boolean m_ascend;

      private SetSorter(boolean ascend) {
         m_ascend = ascend;
      }

      public SetSorter ascend() {
         return ASC;
      }

      public SetSorter descend() {
         return DESC;
      }

      public <T> Set<T> sort(Set<T> set, final Comparator<T> comparator) {
         Set<T> sorted = new TreeSet<T>(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
               int result = comparator.compare(o1, o2);

               return m_ascend ? result : -result;
            }
         });

         sorted.addAll(set);
         return sorted;
      }
   }
}
