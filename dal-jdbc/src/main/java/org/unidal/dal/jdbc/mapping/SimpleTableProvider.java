package org.unidal.dal.jdbc.mapping;

import java.util.Map;

import org.unidal.lookup.annotation.Named;

@Named(type = TableProvider.class, value="undefined")
public class SimpleTableProvider implements TableProvider {
   private String m_dataSourceName;

   private String m_physicalTableName;

   public String getDataSourceName(Map<String, Object> hints, String logicalTableName) {
      return m_dataSourceName;
   }

   public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName) {
      return m_physicalTableName;
   }

   public void setDataSourceName(String dataSourceName) {
      m_dataSourceName = dataSourceName;
   }

   public void setPhysicalTableName(String physicalTableName) {
      m_physicalTableName = physicalTableName;
   }
}
