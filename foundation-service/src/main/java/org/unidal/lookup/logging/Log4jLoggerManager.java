package org.unidal.lookup.logging;

import static org.unidal.lookup.logging.Logger.LEVEL_DEBUG;
import static org.unidal.lookup.logging.Logger.LEVEL_ERROR;
import static org.unidal.lookup.logging.Logger.LEVEL_INFO;
import static org.unidal.lookup.logging.Logger.LEVEL_WARN;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.unidal.helper.Properties;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;

public class Log4jLoggerManager extends AbstractLoggerManager implements LoggerManager, Initializable {
	private String m_configurationFile = "log4j.xml";

	private String m_baseDirRef;

	@Override
	protected org.unidal.lookup.logging.AbstractLogger createLogger(String name) {
		Logger logger = LogManager.getLogger(name);
		LevelAdapter level = new LevelAdapter(logger.getLevel());

		return new LoggerAdapter(logger, level.getThreshold());
	}

	@Override
	public void initialize() throws InitializationException {
		String baseDir = Properties.forString().fromSystem().fromEnv().getProperty(m_baseDirRef, null);
		boolean loaded = false;

		if (baseDir != null) {
			loaded = tryLoadingFromFile(baseDir);
		}

		if (!loaded) {
			loaded = tryLoadingFromResource();
		}

		if (!loaded) {
			LogManager.getLogger(getClass().getName()).warn(
			      String.format("No configuration(%s) was found with baseDirRef(%s)!", m_configurationFile, m_baseDirRef));
		}
	}

	public void setBaseDirRef(String baseDirRef) {
		m_baseDirRef = baseDirRef;
	}

	public void setConfigurationFile(String configurationFile) {
		m_configurationFile = configurationFile;
	}

	private boolean tryLoadingFromFile(String baseDir) throws FactoryConfigurationError {
		if (m_configurationFile.endsWith(".xml")) {
			File file = new File(baseDir, m_configurationFile);

			if (file.exists()) {
				DOMConfigurator.configure(file.getAbsolutePath());
				return true;
			}
		} else if (m_configurationFile.endsWith(".properties")) {
			File file = new File(baseDir, m_configurationFile);

			if (file.exists()) {
				PropertyConfigurator.configure(file.getAbsolutePath());
				return true;
			}
		}

		return false;
	}

	private boolean tryLoadingFromResource() throws FactoryConfigurationError {
		if (m_configurationFile.endsWith(".xml")) {
			URL url = getClass().getResource(m_configurationFile);

			if (url != null) {
				DOMConfigurator.configure(url);
				return true;
			}
		} else if (m_configurationFile.endsWith(".properties")) {
			URL url = getClass().getResource(m_configurationFile);

			if (url != null) {
				PropertyConfigurator.configure(url);
				return true;
			}
		}

		return false;
	}

	static class LevelAdapter {
		private int m_threshold;

		public LevelAdapter(Level level) {
			m_threshold = LEVEL_DEBUG;

			if (level != null) {
				switch (level.toInt()) {
				case Priority.DEBUG_INT:
					m_threshold = LEVEL_DEBUG;
					break;
				case Priority.INFO_INT:
					m_threshold = LEVEL_INFO;
					break;
				case Priority.WARN_INT:
					m_threshold = LEVEL_WARN;
					break;
				case Priority.ERROR_INT:
					m_threshold = LEVEL_ERROR;
					break;
				}
			}
		}

		public int getThreshold() {
			return m_threshold;
		}
	}

	static class LoggerAdapter extends AbstractLogger {
		private Logger m_logger;

		public LoggerAdapter(Logger logger, int threshold) {
			super(threshold, logger.getName());

			m_logger = logger;
		}

		@Override
		public void debug(String message, Throwable t) {
			if (isDebugEnabled()) {
				m_logger.debug(message, t);
			}
		}

		@Override
		public void error(String message, Throwable t) {
			if (isErrorEnabled()) {
				m_logger.error(message, t);
			}
		}

		@Override
		public void info(String message, Throwable t) {
			if (isInfoEnabled()) {
				m_logger.info(message, t);
			}
		}

		@Override
		public void warn(String message, Throwable t) {
			if (isWarnEnabled()) {
				m_logger.warn(message, t);
			}
		}
	}
}
