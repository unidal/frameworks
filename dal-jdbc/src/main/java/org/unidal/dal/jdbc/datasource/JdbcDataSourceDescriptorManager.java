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

public class JdbcDataSourceDescriptorManager implements Initializable, LogEnabled {
   private String m_datasourceFile;

   private DataSourcesDef m_dataSources;

   private Map<String, JdbcDataSourceDescriptor> m_descriptors = new HashMap<String, JdbcDataSourceDescriptor>();

   private String m_baseDirRef;

   private Logger m_logger;

   private String m_defaultBaseDir;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   protected JdbcDataSourceDescriptor getDescriptor(DataSourceDef ds) {
      JdbcDataSourceDescriptor d = new JdbcDataSourceDescriptor();
      PropertiesDef properties = ds.getProperties();
      String url = properties.getUrl();
      String connectionProperties = properties.getConnectionProperties();

      if (connectionProperties != null && connectionProperties.length() > 0) {
         d.setProperty("url", url + "?" + connectionProperties);
      } else {
         d.setProperty("url", url);
      }

      d.setId(ds.getId());
      d.setType(ds.getType());
      d.setProperty("driver", properties.getDriver());
      d.setProperty("user", properties.getUser());
      d.setProperty("password", properties.getPassword());
      d.setProperty("login-timeout", toTime(ds.getConnectionTimeout()));
      d.setProperty("max-idle-time", toTime(ds.getIdleTimeout()));
      d.setProperty("min-pool-size", ds.getMinimumPoolSize());
      d.setProperty("max-pool-size", ds.getMaximumPoolSize());

      return d;
   }

   public JdbcDataSourceDescriptor getDescriptor(String id) {
      JdbcDataSourceDescriptor configuration = m_descriptors.get(id);

      if (configuration == null) {
         if (m_dataSources != null && id != null) {
            DataSourceDef ds = m_dataSources.findDataSource(id);

            if (ds != null) {
               configuration = getDescriptor(ds);
               m_descriptors.put(id, configuration);
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
            m_logger.info(String.format("Loading data sources from %s ...", file));

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
