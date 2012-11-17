package org.unidal.dal.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

public class JndiDataSource implements DataSource, Initializable {
	private String m_jndiName;

	private DataSource m_cpds;

	@Override
	public Connection getConnection() throws SQLException {
		return m_cpds.getConnection();
	}

	public void initialize() throws InitializationException {
		Object obj;

		try {
			obj = new InitialContext().lookup(m_jndiName);
		} catch (NamingException e) {
			throw new DataSourceException("No JNDI entry(" + m_jndiName + ") defined for DataSource, message: " + e, e);
		}

		try {
			m_cpds = (DataSource) obj;
		} catch (Exception e) {
			throw new DataSourceException("Error when looking up data source(" + m_jndiName
			      + "), expected: javax.sql.ConnectionPoolDataSource. got: " + obj.getClass().getName());
		}

		makeFirstConnection();
	}

	protected void makeFirstConnection() {
		try {
			Connection connection = m_cpds.getConnection();

			connection.close();
		} catch (SQLException e) {
			throw new DataSourceException("Error when connecting to data source(" + m_jndiName + "), message: " + e, e);
		}
	}

	public void setJndiName(String jndiName) {
		m_jndiName = jndiName;
	}
}
