package org.unidal.dal.jdbc.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.transform.DefaultSaxParser;
import org.unidal.helper.Properties;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = DataSourceProvider.class)
public class DefaultDataSourceProvider implements DataSourceProvider, LogEnabled {
   private String m_datasourceFile;

   private String m_baseDirRef;

   private String m_defaultBaseDir;

   private Logger m_logger;

   private DataSourcesDef m_def;

   @Override
   public DataSourcesDef defineDatasources() {
      if (m_def == null) {
         if (m_datasourceFile != null) {
            // check configuration file from file system for most case
            File file;
            InputStream is = null;

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

            try {
               file = file.getCanonicalFile();
            } catch (IOException e1) {
               // ignore it
            }
            
            if (file.canRead()) {
               m_logger.info(String.format("Loading data sources from %s ...", file));

               try {
                  is = new FileInputStream(file);
               } catch (FileNotFoundException e) {
                  // ignore it
               }
            } else {
               // check configuration file from classpath for hadoop map-reduce jobs etc.
               // since it could be distributed everywhere and there is no configuration file available during runtime environment
               is = Thread.currentThread().getContextClassLoader().getResourceAsStream(m_datasourceFile);

               if (is == null) {
                  is = getClass().getResourceAsStream(m_datasourceFile);
               }

               if (is != null) {
                  m_logger.info(String.format("Loading data sources from resource(%s)", m_datasourceFile));
               } else {
                  m_logger.warn(String.format("Data sources configuration(%s) is not found!", file));
               }
            }

            if (is != null) {
               try {
                  m_def = DefaultSaxParser.parse(is);
               } catch (Exception e) {
                  throw new IllegalStateException("Error when loading data sources file: " + file, e);
               }
            } else {
               m_def = new DataSourcesDef();
            }
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
