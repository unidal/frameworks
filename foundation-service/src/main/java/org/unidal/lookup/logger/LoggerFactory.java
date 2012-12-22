package org.unidal.lookup.logger;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.unidal.lookup.ContainerLoader;

public class LoggerFactory {
   private static Logger s_logger;

   public static synchronized Logger getLogger(Class<?> clazz) {
      if (s_logger == null) {
         try {
            LoggerManager loggerManager = ContainerLoader.getDefaultContainer().lookup(LoggerManager.class);

            s_logger = loggerManager.getLoggerForComponent(clazz.getName());
         } catch (Exception e) {
            throw new IllegalStateException("Error when getting logger from the container!", e);
         }
      }

      return s_logger;
   }
}
