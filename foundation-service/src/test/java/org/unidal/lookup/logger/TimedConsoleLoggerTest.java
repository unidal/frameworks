package org.unidal.lookup.logger;

import org.junit.Test;

public class TimedConsoleLoggerTest {
	@Test
	public void testWithoutPattern() {
		TimedConsoleLogger logger = new TimedConsoleLogger(TimedConsoleLogger.LEVEL_INFO, "test", "MM-dd HH:mm:ss.SSS",
		      "target/logger/test.log", true, false);

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
	}
}
