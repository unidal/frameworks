package org.unidal.concurrent.internals;

import java.lang.reflect.Array;

import org.unidal.concurrent.StageStatus;

public class DefaultStageStatus implements StageStatus {
   private int m_intervalInMillis;

   private int[] m_available;

   private long[] m_counts;

   private int[] m_costs;

   public DefaultStageStatus(int intervalInMillis) {
      m_intervalInMillis = intervalInMillis;
   }

   public int checkThroughput(StageStatus last) {
      long totalCount = 0;
      int totalCost = 0;

      for (long count : m_counts) {
         totalCount += count;
      }

      for (int cost : m_costs) {
         totalCost += cost;
      }

      int result = 0;

      if (totalCount == 0) {
         result = -1;
      } else if (totalCost > 0) {
         int threads = getThreads();

         if (totalCost > threads * 700) {
            result = 1;
         } else if (totalCost < (threads - 1) * 700) {
            result = -1;
         }
      }

      return result;
   }

   // TODO remove it
   int checkCapacity2() {
      int totalAvailable = 0;
      long totalCount = 0;
      int totalCost = 0;

      for (int available : m_available) {
         totalAvailable += available;
      }

      for (long count : m_counts) {
         totalCount += count;
      }

      for (int cost : m_costs) {
         totalCost += cost;
      }

      int result = 0;

      if (totalCount == 0) {
         result = -1;
      } else if (totalCost > 0) {
         int threads = getThreads();
         long capacity = totalCount * threads * m_intervalInMillis / totalCost;

         if (totalAvailable * 10 >= capacity) {
            result = 1; // more thread
         } else {
            if (capacity * (threads - 1) < totalCount) {
               result = -1; // less thread
            }
         }
      }

      return result;
   }

   @Override
   public int getActors() {
      return m_available.length;
   }

   @Override
   public int[] getAvailable() {
      return m_available;
   }

   @Override
   public int getIntervalInMillis() {
      return m_intervalInMillis;
   }

   @Override
   public int[] getProcessedCosts() {
      return m_costs;
   }

   @Override
   public long[] getProcessedCounts() {
      return m_counts;
   }

   @Override
   public int getThreads() {
      return m_counts.length;
   }

   public void setAvailableCounts(int[] available) {
      m_available = available;
   }

   public void setProcessed(int[] costs, long[] counts) {
      m_costs = costs;
      m_counts = counts;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(1024);

      sb.append(getClass().getSimpleName()).append('[');

      sb.append("interval=").append(m_intervalInMillis).append(", ");
      sb.append("actors=").append(getActors()).append(", ");
      sb.append("available=").append(toString(m_available)).append(", ");
      sb.append("threads=").append(getThreads()).append(", ");
      sb.append("processed=").append(toString(m_counts)).append(", ");
      sb.append("costs=").append(toString(m_costs)).append(", ");

      sb.setLength(sb.length() - 2);
      sb.append(']');

      return sb.toString();
   }

   private String toString(Object array) {
      int len = Array.getLength(array);
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < len; i++) {
         if (i > 0) {
            sb.append(',');
         } else {
            sb.append('[');
         }

         sb.append(Array.get(array, i));

         if (i == len - 1) {
            sb.append(']');
         }
      }

      return sb.toString();
   }
}
