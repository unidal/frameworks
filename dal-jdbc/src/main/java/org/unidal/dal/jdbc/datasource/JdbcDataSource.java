package org.unidal.dal.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.unidal.cat.Cat;
import org.unidal.helper.Codes;
import org.unidal.helper.Splitters;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Disposable;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Named(type = DataSource.class, value = "jdbc", instantiationStrategy = Named.PER_LOOKUP)
public class JdbcDataSource implements DataSource, LogEnabled, Disposable {
   private ComboPooledDataSource m_cpds;

   private Logger m_logger;

   private DataSourceDescriptor m_descriptor;

   private String decode(String src) {
      if (src == null) {
         return null;
      }

      if (src.startsWith("~{") && src.endsWith("}")) {
         try {
            return Codes.forDecode().decode(src.substring(2, src.length() - 1));
         } catch (Exception e) {
            Cat.logError("Unable to decode value: " + src, e);
         }
      }

      return src;
   }

   @Override
   public void dispose() {
      m_cpds.close();
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   @Override
   public Connection getConnection() throws SQLException {
      return m_cpds.getConnection();
   }

   @Override
   public DataSourceDescriptor getDescriptor() {
      return m_descriptor;
   }

   @Override
   public void initialize(DataSourceDescriptor d) {
      m_descriptor = d;

      ComboPooledDataSource cpds = new ComboPooledDataSource();
      String id = d.getId();
      String url = d.getProperty("url", null);
      String driver = d.getProperty("driver", null);
      String user = d.getProperty("user", null);

      try {
         cpds.setDriverClass(driver);
         cpds.setJdbcUrl(url);
         cpds.setUser(user);
         cpds.setPassword(decode(d.getProperty("password", null)));
         cpds.setMinPoolSize(d.getIntProperty("min-pool-size", 1));
         cpds.setMaxPoolSize(d.getIntProperty("max-pool-size", 3));
         cpds.setInitialPoolSize(d.getIntProperty("initial-pool-size", 1));
         cpds.setMaxIdleTime(d.getIntProperty("max-idle-time", 10 * 60));
         cpds.setIdleConnectionTestPeriod(d.getIntProperty("idel-connection-test-period", 60));
         cpds.setAcquireRetryAttempts(d.getIntProperty("accquire-retry-attempts", 1));
         cpds.setAcquireRetryDelay(d.getIntProperty("accquire-retry-delay", 30));
         cpds.setMaxStatements(0);
         cpds.setMaxStatementsPerConnection(1000);
         cpds.setNumHelperThreads(6);
         cpds.setMaxAdministrativeTaskTime(5);
         cpds.setPreferredTestQuery("SELECT 1");
         cpds.setLoginTimeout(d.getIntProperty("login-timeout", 30));
         cpds.setCheckoutTimeout(d.getIntProperty("checkout-timeout", 0));

         setConnectionProperties(cpds, d.getProperty("connectionProperties", null));

         m_logger.info(String.format("Connecting to JDBC data source(%s) "
               + "with properties(driver=%s, url=%s, user=%s) ...", id, driver, url, user));
         m_cpds = cpds;
         m_cpds.getConnection().close();
         m_logger.info(String.format("Connected to JDBC data source(%s).", id));
      } catch (Throwable e) {
         cpds.close();

         throw new DataSourceException(String.format("Error when connecting to JDBC data source(%s) "
               + "with properties (driver=%s, url=%s, user=%s). Error message=%s", id, driver, url, user, e), e);
      }
   }

   private void setConnectionProperties(ComboPooledDataSource cpds, String connectionProperties) {
      Map<String, String> properties = Splitters.by('&', '=').trim().split(connectionProperties);
      boolean hasRewriteBatchedStatements = false;

      for (Map.Entry<String, String> e : properties.entrySet()) {
         String key = e.getKey();

         if (key.equals("rewriteBatchedStatements")) {
            hasRewriteBatchedStatements = true;
         }

         cpds.getProperties().setProperty(key, e.getValue());
      }

      if (!hasRewriteBatchedStatements) {
         cpds.getProperties().setProperty("rewriteBatchedStatements", "true");
      }
   }
}
