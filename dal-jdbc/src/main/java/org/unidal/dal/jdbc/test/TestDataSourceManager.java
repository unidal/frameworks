package org.unidal.dal.jdbc.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceDescriptor;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptor;

public class TestDataSourceManager implements DataSourceManager {
   @Override
   public List<String> getDataSourceNames() {
      throw new UnsupportedOperationException("Not used yet!");
   }

   @Override
   public DataSource getDataSource(String dataSourceName) {
      return new TestDataSource(dataSourceName);
   }

   static class TestDataSource implements DataSource {
      private JdbcDataSourceDescriptor m_descriptor;

      private JdbcConnectionPool m_pool;

      public TestDataSource(String dataSourceName) {
         JdbcDataSourceDescriptor descriptor = new JdbcDataSourceDescriptor();
         String url = String.format("jdbc:h2:mem:%s?MODE=MySQL", dataSourceName);

         descriptor.setId(dataSourceName);
         descriptor.setType("h2");
         descriptor.setProperty("url", url);

         m_descriptor = descriptor;
         m_pool = JdbcConnectionPool.create(url, "sa", "sa");
      }

      @Override
      public Connection getConnection() throws SQLException {
         return m_pool.getConnection();
      }

      @Override
      public DataSourceDescriptor getDescriptor() {
         return m_descriptor;
      }

      @Override
      public void initialize(DataSourceDescriptor descriptor) {
      }
   }
}