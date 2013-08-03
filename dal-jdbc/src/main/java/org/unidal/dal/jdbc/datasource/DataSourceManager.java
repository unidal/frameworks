package org.unidal.dal.jdbc.datasource;

import java.util.List;

public interface DataSourceManager {
   public List<String> getDataSourceNames();

   public DataSource getDataSource(String dataSourceName);
}
