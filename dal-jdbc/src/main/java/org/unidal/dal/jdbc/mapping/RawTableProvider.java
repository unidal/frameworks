package org.unidal.dal.jdbc.mapping;

import java.util.Map;

public class RawTableProvider implements TableProvider {
   private String m_logicalTableName;

   private static ThreadLocal<String> s_threadLocalData = new ThreadLocal<String>() {
      @Override
      protected String initialValue() {
         throw new UnsupportedOperationException("Please call RawTableProvider.setDataSourceName(...) first!");
      }
   };

   public static void reset() {
      s_threadLocalData.remove();
   }

   public static void setDataSourceName(String dataSourceName) {
      s_threadLocalData.set(dataSourceName);
   }

   @Override
   public String getDataSourceName(Map<String, Object> hints) {
      return s_threadLocalData.get();
   }

   @Override
   public String getLogicalTableName() {
      return m_logicalTableName;
   }

   @Override
   public String getPhysicalTableName(Map<String, Object> hints) {
      throw new UnsupportedOperationException("This table provider is only used by RawDao!");
   }

   public void setLogicalTableName(String logicalTableName) {
      m_logicalTableName = logicalTableName;
   }
}
