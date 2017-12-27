package org.unidal.lookup.logging;

import java.util.HashSet;
import java.util.Set;

import org.unidal.lookup.annotation.Named;

@Named(type = LoggerManager.class)
public class TimedConsoleLoggerManager extends AbstractLoggerManager {
   private static Set<String> s_skipedClassNames = new HashSet<String>();

   private String m_dateFormat = "MM-dd HH:mm:ss.SSS";

   private boolean m_showClass = true;

   private int m_threshold = Logger.LEVEL_INFO;

   private AbstractLogger m_logger;

   public static boolean shouldSkipClass(String className) {
      return s_skipedClassNames.contains(className);
   }

   public static void skipClass(Class<?> clazz) {
      s_skipedClassNames.add(clazz.getName());
   }

   @Override
   public AbstractLogger createLogger(String name) {
      if (m_logger == null) {
         synchronized (this) {
            if (m_logger == null) {
               TimedConsoleLogger logger = new TimedConsoleLogger(m_threshold, name, m_dateFormat, m_showClass);

               m_logger = logger;
            }
         }
      }

      return m_logger;
   }

   public void setDateFormat(String dateFormat) {
      m_dateFormat = dateFormat;
   }

   public void setShowClass(boolean showClass) {
      m_showClass = showClass;
   }

   @Deprecated
   public void setLogFilePattern(String logFilePattern) {
   }

   @Deprecated
   public void setBaseDirRef(String baseDirRef) {
   }

   @Deprecated
   public void setDefaultBaseDir(String defaultBaseDir) {
   }
}
