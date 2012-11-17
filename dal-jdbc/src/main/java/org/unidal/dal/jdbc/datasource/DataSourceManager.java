package org.unidal.dal.jdbc.datasource;

import java.util.List;

public interface DataSourceManager {
	public List<String> getActiveDataSourceNames();

	public DataSource getDataSource(String dataSourceName);

	public JdbcDataSourceConfiguration getDataSourceConfiguration(String dataSourceName);
}
