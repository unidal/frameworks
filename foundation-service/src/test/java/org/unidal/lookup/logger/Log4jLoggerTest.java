package org.unidal.lookup.logger;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class Log4jLoggerTest extends ComponentTestCase {
   @Test
   public void test() {
      LoggerManager manager = lookup(LoggerManager.class);
      Logger logger = manager.getLoggerForComponent(getClass().getName());

      logger.info("hello world");
   }
}
