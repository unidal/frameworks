package org.unidal.dal.jdbc.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourceDef;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.entity.PropertiesDef;
import org.unidal.dal.jdbc.datasource.model.transform.DefaultSaxParser;
import org.unidal.helper.Properties;

public class JdbcDataSourceConfigurationManager implements Initializable, LogEnabled {
   private String m_datasourceFile;

   private DataSourcesDef m_dataSources;

   private Map<String, JdbcDataSourceConfiguration> m_configurations = new HashMap<String, JdbcDataSourceConfiguration>();

   private String m_baseDirRef;

   private Logger m_logger;

   private String m_defaultBaseDir;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   protected JdbcDataSourceConfiguration getConfiguration(DataSourceDef ds) {
      JdbcDataSourceConfiguration configuration = new JdbcDataSourceConfiguration();
      PropertiesDef properties = ds.getProperties();

      configuration.setId(ds.getId());
      configuration.setConnectionTimeout(toTime(ds.getConnectionTimeout()));
      configuration.setIdleTimeout(toTime(ds.getIdleTimeout()));
      configuration.setMaximumPoolSize(ds.getMaximumPoolSize());
      configuration.setStatementCacheSize(ds.getStatementCacheSize());
      configuration.setDriver(properties.getDriver());

      String connectionProperties = properties.getConnectionProperties();

      if (connectionProperties != null && connectionProperties.length() > 0) {
         configuration.setUrl(properties.getUrl() + "?" + connectionProperties);
      } else {
         configuration.setUrl(properties.getUrl());
      }

      configuration.setUser(properties.getUser());
      configuration.setPassword(properties.getPassword());

      return configuration;
   }

   public JdbcDataSourceConfiguration getConfiguration(String id) {
      JdbcDataSourceConfiguration configuration = m_configurations.get(id);

      if (configuration == null) {
         if (m_dataSources != null && id != null) {
            DataSourceDef ds = m_dataSources.findDataSource(id);

            if (ds != null) {
               configuration = getConfiguration(ds);
               m_configurations.put(id, configuration);
            }
         }
      }

      return configuration;
   }

   public List<String> getDataSourceNames() {
      List<String> names = new ArrayList<String>();

      for (DataSourceDef ds : m_dataSources.getDataSourcesMap().values()) {
         names.add(ds.getId());
      }

      return names;
   }

   public void initialize() throws InitializationException {
      if (m_datasourceFile != null) {
         InputStream is = null;

         // check configuration file from file system for most case
         File file;

         if (m_datasourceFile.startsWith("/")) {
            file = new File(m_datasourceFile);
         } else {
            String baseDir = Properties.forString().fromEnv().fromSystem().getProperty(m_baseDirRef, m_defaultBaseDir);

            if (baseDir != null) {
               file = new File(baseDir, m_datasourceFile);
            } else {
               file = new File(m_datasourceFile);
            }
         }

         if (file.canRead()) {
            m_logger.info(String.format("Loading data sources from %s", file));

            try {
               is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
               // ignore it
            }
         } else {
            m_logger.warn(String.format("Data source configuration(%s) is not found!", file, m_datasourceFile));

            // check configuration file from classpath for hadoop map-reduce jobs etc.
            // since it's distributed everywhere and no configuration file during runtime environment
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(m_datasourceFile);

            if (is == null) {
               is = getClass().getResourceAsStream(m_datasourceFile);
            }

            if (is != null) {
               m_logger.info(String.format("Loading data sources from resource(%s)", m_datasourceFile));
            }
         }

         if (is != null) {
            try {
               m_dataSources = DefaultSaxParser.parse(is);

            } catch (Exception e) {
               throw new InitializationException("Error when loading data source file: " + file, e);
            }
         }
      }
   }

   public void setBaseDirRef(String baseDirRef) {
      m_baseDirRef = baseDirRef;
   }

   public void setDatasourceFile(String datasourceFile) {
      m_datasourceFile = datasourceFile;
   }

   public void setDefaultBaseDir(String defaultBaseDir) {
      m_defaultBaseDir = defaultBaseDir;
   }

   protected int toTime(String source) {
      int time = 0;
      int len = source == null ? 0 : source.length();

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
}
