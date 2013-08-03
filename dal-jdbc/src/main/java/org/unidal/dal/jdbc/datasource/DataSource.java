package org.unidal.dal.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
   public Connection getConnection() throws SQLException;

   public DataSourceDescriptor getDescriptor();

   public void initialize(DataSourceDescriptor descriptor);
}
