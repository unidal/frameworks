package org.unidal.dal.jdbc.mapping;

import java.util.Map;

import org.unidal.lookup.annotation.Named;

@Named(type = TableProvider.class, value = "raw")
public class RawTableProvider implements TableProvider {
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
   public String getDataSourceName(Map<String, Object> hints, String logicalTableName) {
      return s_threadLocalData.get();
   }

   @Override
   public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName) {
      throw new UnsupportedOperationException("This table provider is only used by RawDao!");
   }
}
