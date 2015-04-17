package org.unidal.dal.jdbc.mapping;

import java.util.Map;

public interface TableProvider {
   public String getDataSourceName(Map<String, Object> hints, String logicalTableName);

   public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName);
}
