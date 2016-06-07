package org.unidal.lookup.logger;

import java.io.File;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TimedConsoleLoggerTest {
   @Test
   public void testWithBaseDirRef() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            "testForBaseDirRef.log", true, false);

      logger.setBaseDirRef("HOME");
      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      File target = new File(System.getenv("HOME"), "testForBaseDirRef.log");

      Assert.assertTrue(String.format("Target file(%s) is not created!", target), target.exists());
      target.deleteOnExit();
   }

   @Test
   public void testWithDefaultBaseDir() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            "testForBaseDirRef.log", true, false);

      logger.setDefaultBaseDir("target");
      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      File target = new File("target/testForBaseDirRef.log");

      Assert.assertTrue(String.format("Target file(%s) is not created!", target), target.exists());
      target.deleteOnExit();
   }

   @Test
   public void testWithoutPattern() {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            "target/logger/test.log", false, false);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");
   }

   @Test
   public void testWithPattern() throws Exception {
      TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
            "target/logger/test_{0,date,ss}.log", true, false);

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
            "target/logger/test_{0,date,ss}.log", true, true);

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
            "target/logger/test_{0,date,ss}.log", true, false);

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      logger.debug("zero");
      logger.info("first");
      logger.warn("second");

      System.setProperty("devMode", "false");
   }
}
