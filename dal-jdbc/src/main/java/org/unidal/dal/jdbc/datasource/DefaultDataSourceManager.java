package org.unidal.dal.jdbc.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

public class DefaultDataSourceManager extends ContainerHolder implements DataSourceManager {
	@Inject
	private JdbcDataSourceConfigurationManager m_configurationManager;

	private Map<String, DataSource> m_dataSources = new HashMap<String, DataSource>();

	public JdbcDataSourceConfiguration getDataSourceConfiguration(String dataSourceName) {
		return m_configurationManager.getConfiguration(dataSourceName);
	}

	@Override
	public DataSource getDataSource(String dataSourceName) {
		DataSource dataSource = m_dataSources.get(dataSourceName);

		if (dataSource == null) {
			synchronized (m_dataSources) {
				dataSource = m_dataSources.get(dataSourceName);

				if (dataSource == null) {
					dataSource = lookup(DataSource.class, dataSourceName);
					m_dataSources.put(dataSourceName, dataSource);
				}
			}
		}

		return dataSource;
	}

	@Override
	public List<String> getActiveDataSourceNames() {
		List<String> list = new ArrayList<String>(m_dataSources.keySet());

		Collections.sort(list);
		return list;
	}
}
