package org.unidal.lookup.logger;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

public class TimedConsoleLoggerManager extends ConsoleLoggerManager {
   private String m_dateFormat = "MM-dd HH:mm:ss.SSS";

   private String m_logFilePattern;

   private String m_baseDirRef;

   private boolean m_showClass;

   private boolean m_devMode;

   private String m_defaultBaseDir;

   @Override
   public Logger createLogger(int threshold, String name) {
      TimedConsoleLogger logger = new TimedConsoleLogger(threshold, name, m_dateFormat, m_logFilePattern, m_showClass, m_devMode);

      logger.setBaseDirRef(m_baseDirRef);
      logger.setDefaultBaseDir(m_defaultBaseDir);
      return logger;
   }

   public void setBaseDirRef(String baseDirRef) {
      m_baseDirRef = baseDirRef;
   }

   public void setDefaultBaseDir(String defaultBaseDir) {
      m_defaultBaseDir = defaultBaseDir;
   }

   public void setDateFormat(String dateFormat) {
      m_dateFormat = dateFormat;
   }

   public void setDevMode(boolean devMode) {
      m_devMode = devMode;
   }

   public void setLogFilePattern(String logFilePattern) {
      m_logFilePattern = logFilePattern;
   }

   public void setShowClass(boolean showClass) {
      m_showClass = showClass;
   }
}
