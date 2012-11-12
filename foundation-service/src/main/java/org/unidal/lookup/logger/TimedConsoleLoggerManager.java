package org.unidal.lookup.logger;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

public class TimedConsoleLoggerManager extends ConsoleLoggerManager {
	private String m_dateFormat = "MM-dd HH:mm:ss.SSS";

	private String m_logFilePattern;

	private boolean m_showClass;

	private boolean m_devMode;

	@Override
	public Logger createLogger(int threshold, String name) {
		return new TimedConsoleLogger(threshold, name, m_dateFormat, m_logFilePattern, m_showClass, m_devMode);
	}

	public void setDateFormat(String dateFormat) {
		m_dateFormat = dateFormat;
	}

	public void setLogFilePattern(String logFilePattern) {
		m_logFilePattern = logFilePattern;
	}

	public void setShowClass(boolean showClass) {
		m_showClass = showClass;
	}
	
	public void setDevMode(boolean devMode) {
		m_devMode = devMode;
	}
}
