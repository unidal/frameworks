package org.unidal.lookup.logger;

import org.junit.Ignore;
import org.junit.Test;
import org.unidal.lookup.logging.TimedConsoleLogger;

@Ignore
public class TimedConsoleLoggerTest {
   @Test
   public void testWithBaseDirRef() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            true);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithDefaultBaseDir() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            true);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithoutPattern() {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            false);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithPattern() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            true);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      Thread.sleep(1000L);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithPatternInDevMode() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            true);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithPatternInDevModeByCLI() throws Exception {
      System.setProperty("devMode", "true");

      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            true);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      System.setProperty("devMode", "false");
   }
}
