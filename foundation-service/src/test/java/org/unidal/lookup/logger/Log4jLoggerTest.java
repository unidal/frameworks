package org.unidal.lookup.logger;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.logging.LoggerManager;

public class Log4jLoggerTest extends ComponentTestCase {
   @Test
   public void test() {
      LoggerManager manager = lookup(LoggerManager.class);
      Logger logger = manager.getLoggerForComponent(getClass().getName());

      logger.info("hello world");
   }
}
