package org.unidal.dal.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcDataSource implements DataSource, Initializable, LogEnabled, Disposable {
	private JdbcDataSourceConfigurationManager m_manager;

	private Map<String, String> m_properties;

	private JdbcDataSourceConfiguration m_configuration;

	private ComboPooledDataSource m_cpds;

	private Logger m_logger;

	public JdbcDataSource() {
		m_configuration = new JdbcDataSourceConfiguration();

		// setup default values
		m_configuration.setMaximumPoolSize(3);
		m_configuration.setConnectionTimeout(1000L);
		m_configuration.setIdleTimeout(600 * 1000L); // 10 minutes
		m_configuration.setStatementCacheSize(1000);
		m_properties = Collections.emptyMap();
	}

	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return m_cpds.getConnection();
	}

	public String getUrl() {
		return m_properties.get("URL");
	}
	
	public void initialize() throws InitializationException {
		JdbcDataSourceConfiguration c = prepareConfiguration();

		try {
			ComboPooledDataSource cpds = new ComboPooledDataSource();

			cpds.setDriverClass(c.getDriver());
			cpds.setJdbcUrl(c.getUrl());
			cpds.setUser(c.getUser());
			cpds.setPassword(c.getPassword());
			cpds.setMinPoolSize(2);
			cpds.setInitialPoolSize(2);
			cpds.setMaxPoolSize(c.getMaximumPoolSize());
			cpds.setMaxIdleTime((int) (c.getIdleTimeout() / 1000));
			cpds.setIdleConnectionTestPeriod(60);
			cpds.setAcquireRetryAttempts(3);
			cpds.setAcquireRetryDelay(300);
			cpds.setMaxStatements(0);
			cpds.setMaxStatementsPerConnection(c.getStatementCacheSize());
			cpds.setNumHelperThreads(6);
			cpds.setMaxAdministrativeTaskTime(5);
			cpds.setPreferredTestQuery("SELECT 1");
			cpds.setLoginTimeout((int) c.getConnectionTimeout());

			m_cpds = cpds;
			m_cpds.getConnection().close();
			m_logger.info("Connected to JDBC data source using (" + c.getDriver() + ", " + c.getUrl() + ", user="
			      + c.getUser() + ")");
		} catch (Exception e) {
			throw new DataSourceException("Error when initializing data source using(" + c.getDriver() + ", " + c.getUrl()
			      + ", user=" + c.getUser() + "), message: " + e, e);
		}
	}

	private JdbcDataSourceConfiguration prepareConfiguration() {
		String connectionProperties = m_properties.get("connectionProperties");

		m_configuration.setDriver(m_properties.get("driver"));

		if (connectionProperties != null && connectionProperties.length() > 0) {
			m_configuration.setUrl(m_properties.get("URL") + "?" + connectionProperties);
		} else {
			m_configuration.setUrl(m_properties.get("URL"));
		}

		m_configuration.setUser(m_properties.get("user"));
		m_configuration.setPassword(m_properties.get("password"));

		// merge data from external ds.xml file
		String id = m_configuration.getId();
		JdbcDataSourceConfiguration configuration = m_manager.getConfiguration(id);

		if (configuration != null) {
			configuration.mergeTo(m_configuration);
		}

		return m_configuration;
	}

	public void setConfiguration(JdbcDataSourceConfiguration configuration) {
		configuration.mergeTo(m_configuration);
	}

	public void setConnectionTimeout(String connectionTimeout) {
		m_configuration.setConnectionTimeout(toTime(connectionTimeout));
	}

	public void setId(String id) {
		m_configuration.setId(id);
	}

	public void setIdleTimeout(String idleTimeout) {
		m_configuration.setIdleTimeout(toTime(idleTimeout));
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		m_configuration.setMaximumPoolSize(maximumPoolSize);
	}

	public void setProperties(PlexusConfiguration config) {
		PlexusConfiguration[] children = config.getChildren();
		Map<String, String> properties = new HashMap<String, String>();

		for (PlexusConfiguration child : children) {
			String name = child.getName();
			String value = child.getValue("");

			properties.put(name, value);
		}

		m_properties = properties;
	}

	public void setStatementCacheSize(int statementCacheSize) {
		m_configuration.setStatementCacheSize(statementCacheSize);
	}

	private int toTime(String source) {
		int time = 0;
		int len = source.length();

		int num = 0;
		for (int i = 0; i < len; i++) {
			char ch = source.charAt(i);

			switch (ch) {
			case 'd':
				time += num * 24 * 60 * 60;
				num = 0;
				break;
			case 'h':
				time += num * 60 * 60;
				num = 0;
				break;
			case 'm':
				time += num * 60;
				num = 0;
				break;
			case 's':
				time += num;
				num = 0;
				break;
			default:
				if (ch >= '0' && ch <= '9') {
					num = num * 10 + (ch - '0');
				} else {
					throw new IllegalArgumentException("Invalid character found: " + ch + ", should be one of [0-9][dhms]");
				}
			}
		}

		return time;
	}

	@Override
	public void dispose() {
		m_cpds.close();
	}
}
