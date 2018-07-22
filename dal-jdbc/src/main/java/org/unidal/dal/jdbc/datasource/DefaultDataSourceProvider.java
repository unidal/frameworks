package org.unidal.dal.jdbc.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.transform.DefaultSaxParser;
import org.unidal.helper.Files;
import org.unidal.helper.Properties;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = DataSourceProvider.class)
public class DefaultDataSourceProvider implements DataSourceProvider, Initializable, LogEnabled {
   private String m_datasourceFile;

   private String m_baseDirRef;

   private String m_defaultBaseDir;

   private Logger m_logger;

   private DataSourcesDef m_def;

   private String m_dataSource;

   @Override
   public DataSourcesDef defineDatasources() {
      if (m_def == null) {
         File file = new File(m_dataSource);

         try {
            file = file.getCanonicalFile();
         } catch (IOException e1) {
            // ignore it
         }

         InputStream in = null;

         if (file.canRead()) {
            try {
               in = new FileInputStream(file);
               m_logger.info(String.format("Loading data sources from %s ...", file));
            } catch (FileNotFoundException e) {
               // ignore it
            }
         } else {
            // check configuration file from classpath for hadoop map-reduce jobs etc.
            // since it could be distributed everywhere and there is no configuration file available during runtime environment
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(m_datasourceFile);

            if (in == null) {
               in = getClass().getResourceAsStream(m_datasourceFile);
            }

            if (in != null) {
               m_logger.info(String.format("Loading data sources from resource(%s)", m_datasourceFile));
            }
         }

         if (in != null) {
            try {
               m_def = DefaultSaxParser.parse(in);
            } catch (Exception e) {
               throw new IllegalStateException("Error when loading data sources file: " + file, e);
            }
         } else {
            m_logger.warn(String.format("Data sources configuration(%s) is not found!", file));
            m_def = new DataSourcesDef();
         }
      }

      return m_def;
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public String getDatasourceFile() {
      return m_datasourceFile;
   }

   @Override
   public void initialize() throws InitializationException {
      String baseDir = Properties.forString().fromEnv().fromSystem().getProperty(m_baseDirRef, m_defaultBaseDir);

      if (baseDir != null) {
         baseDir = Files.forDir().getAbsoluteFile(baseDir);
      }

      m_dataSource = Files.forDir().getAbsoluteFile(baseDir, m_datasourceFile);
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
}
