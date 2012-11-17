package org.unidal.dal.jdbc.datasource;

public class JdbcDataSourceConfiguration {
   private String m_id;

   private int m_maximumPoolSize;

   private long m_connectionTimeout;

   private long m_idleTimeout;

   private int m_statementCacheSize;

   private String m_driver;

   private String m_url;

   private String m_user;

   private String m_password;

   public void mergeTo(JdbcDataSourceConfiguration c) {
      if (m_maximumPoolSize > 0) {
         c.setMaximumPoolSize(m_maximumPoolSize);
      }

      if (m_connectionTimeout > 0) {
         c.setConnectionTimeout(m_connectionTimeout);
      }

      if (m_idleTimeout > 0) {
         c.setIdleTimeout(m_idleTimeout);
      }

      if (m_statementCacheSize > 0) {
         c.setStatementCacheSize(m_statementCacheSize);
      }

      if (m_driver != null) {
         c.setDriver(m_driver);
      }

      if (m_url != null) {
         c.setUrl(m_url);
      }

      if (m_user != null) {
         c.setUser(m_user);
      }

      if (m_password != null) {
         c.setPassword(m_password);
      }
   }

   public String getId() {
      return m_id;
   }

   public void setId(String id) {
      m_id = id;
   }

   public long getConnectionTimeout() {
      return m_connectionTimeout;
   }

   public String getDriver() {
      return m_driver;
   }

   public long getIdleTimeout() {
      return m_idleTimeout;
   }

   public int getMaximumPoolSize() {
      return m_maximumPoolSize;
   }

   public String getPassword() {
      return m_password;
   }

   public int getStatementCacheSize() {
      return m_statementCacheSize;
   }

   public String getUrl() {
      return m_url;
   }

   public String getUser() {
      return m_user;
   }

   public void setConnectionTimeout(long connectionTimeout) {
      m_connectionTimeout = connectionTimeout;
   }

   public void setDriver(String driver) {
      m_driver = driver;
   }

   public void setIdleTimeout(long idleTimeout) {
      m_idleTimeout = idleTimeout;
   }

   public void setMaximumPoolSize(int maximumPoolSize) {
      m_maximumPoolSize = maximumPoolSize;
   }

   public void setPassword(String password) {
      m_password = password;
   }

   public void setStatementCacheSize(int statementCacheSize) {
      m_statementCacheSize = statementCacheSize;
   }

   public void setUrl(String url) {
      m_url = url;
   }

   public void setUser(String user) {
      m_user = user;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(256);

      sb.append("JdbcDataSourceConfiguration[");
      sb.append("driver:").append(m_driver);
      sb.append(",url:").append(m_url);
      sb.append(",user:").append(m_user);
      sb.append(",password:").append(m_password);
      sb.append("]");

      return sb.toString();
   }
}
