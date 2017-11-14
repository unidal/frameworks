package org.unidal.lookup.logging;

import java.text.MessageFormat;
import java.util.Date;

import org.unidal.helper.Threads;

public class TimedConsoleLogger extends AbstractLogger implements Logger {
   private MessageFormat m_format;

   private boolean m_showClass;

   public TimedConsoleLogger(int threshold, String name, String dateFormat, boolean showClass) {
      super(threshold, name);

      String pattern;

      if (showClass) {
         pattern = "[{0,date," + dateFormat + "}] [{1}] [{3}] {2}";
      } else {
         pattern = "[{0,date," + dateFormat + "}] [{1}] {2}";
      }

      m_showClass = showClass;
      m_format = new MessageFormat(pattern);
   }

   @Override
   public void debug(String message, Throwable throwable) {
      if (isDebugEnabled()) {
         out("DEBUG", message, throwable);
      }
   }

   @Override
   public void error(String message, Throwable throwable) {
      if (isErrorEnabled()) {
         out("ERROR", message, throwable);
      }
   }

   private String getCallerClassName() {
      String caller = Threads.getCallerClass();

      if (caller != null) {
         return caller;
      }

      StackTraceElement[] elements = new Exception().getStackTrace();

      if (elements.length > 5) {
         for (int i = 5; i < elements.length; i++) {
            String className = elements[i].getClassName();

            if (TimedConsoleLoggerManager.shouldSkipClass(className)) {
               continue;
            }

            int pos = className.lastIndexOf('$');

            if (pos < 0) {
               pos = className.lastIndexOf('.');
            }

            if (pos > 0) {
               return className.substring(pos + 1);
            } else {
               return className;
            }
         }
      }

      return "N/A";
   }

   private String getTimedMessage(String level, String message) {
      if (m_showClass) {
         return m_format.format(new Object[] { new Date(), level, message, getCallerClassName() });
      } else {
         return m_format.format(new Object[] { new Date(), level, message });
      }
   }

   @Override
   public void info(String message, Throwable throwable) {
      if (isInfoEnabled()) {
         out("INFO", message, throwable);
      }
   }

   private void out(String severity, String message, Throwable throwable) {
      String timedMessage = getTimedMessage(severity, message);

      System.out.println(timedMessage);

      if (throwable != null) {
         throwable.printStackTrace(System.out);
      }
   }

   @Override
   public void warn(String message, Throwable throwable) {
      if (isWarnEnabled()) {
         out("WARN", message, throwable);
      }
   }
}
