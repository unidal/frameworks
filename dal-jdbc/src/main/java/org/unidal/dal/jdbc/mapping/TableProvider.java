package org.unidal.dal.jdbc.mapping;

import java.util.Map;

public interface TableProvider {
   public String getLogicalTableName();

   public String getDataSourceName(Map<String, Object> hints);

   public String getPhysicalTableName(Map<String, Object> hints);
}
