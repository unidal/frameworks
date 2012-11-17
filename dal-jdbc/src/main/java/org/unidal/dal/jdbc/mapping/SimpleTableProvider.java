package org.unidal.dal.jdbc.mapping;

import java.util.Map;

public class SimpleTableProvider implements TableProvider {
   private String m_dataSourceName;

   private String m_logicalTableName;

   private String m_physicalTableName;

   public String getDataSourceName(Map<String, Object> hints) {
      return m_dataSourceName;
   }

   public String getLogicalTableName() {
      return m_logicalTableName;
   }

   public String getPhysicalTableName(Map<String, Object> hints) {
      if (m_physicalTableName != null) {
         return m_physicalTableName;
      } else {
         return m_logicalTableName;
      }
   }

   public void setDataSourceName(String dataSourceName) {
      m_dataSourceName = dataSourceName;
   }

   public void setLogicalTableName(String logicalTableName) {
      m_logicalTableName = logicalTableName;
   }

   public void setPhysicalTableName(String physicalTableName) {
      m_physicalTableName = physicalTableName;
   }
}
