package org.unidal.dal.jdbc.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceDescriptor;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptor;
import org.unidal.lookup.annotation.Named;

@Named(type = DataSourceManager.class)
public class TestDataSourceManager implements DataSourceManager {
   private Map<String, TestDataSource> m_dataSources = new LinkedHashMap<String, TestDataSource>();

   @Override
   public synchronized DataSource getDataSource(String name) {
      TestDataSource dataSource = m_dataSources.get(name);

      if (dataSource == null) {
         synchronized (m_dataSources) {
            dataSource = m_dataSources.get(name);

            if (dataSource == null) {
               dataSource = new TestDataSource(name);

               m_dataSources.put(name, dataSource);
            }
         }
      }

      return dataSource;
   }

   @Override
   public synchronized List<String> getDataSourceNames() {
      return new ArrayList<String>(m_dataSources.keySet());
   }

   static class TestDataSource implements DataSource {
      private JdbcDataSourceDescriptor m_descriptor;

      private JdbcConnectionPool m_pool;

      public TestDataSource(String dataSourceName) {
         JdbcDataSourceDescriptor descriptor = new JdbcDataSourceDescriptor();
         String url = String.format("jdbc:h2:mem:%s;MODE=MySQL", dataSourceName);

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

      public void reset() {
         m_pool.dispose();
      }
   }
}