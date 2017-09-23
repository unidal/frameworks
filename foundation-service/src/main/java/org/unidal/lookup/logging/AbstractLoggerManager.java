package org.unidal.lookup.logging;

import static org.unidal.lookup.logging.Logger.*;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLoggerManager implements LoggerManager {
	private Map<String, Logger> m_loggers = new HashMap<String, Logger>();

	private int m_threshold = Logger.LEVEL_INFO;

	protected abstract AbstractLogger createLogger(String name);

	@Override
	public Logger getLoggerForComponent(String role) {
		Logger logger = m_loggers.get(role);

		if (logger == null) {
			logger = createLogger(role);
			m_loggers.put(role, logger);
		}

		return logger;
	}

	protected int getThreshold() {
		return m_threshold;
	}

	protected boolean isValidThreshold(int threshold) {
		if (threshold == LEVEL_DEBUG) {
			return true;
		}
		if (threshold == LEVEL_INFO) {
			return true;
		}
		if (threshold == LEVEL_WARN) {
			return true;
		}
		if (threshold == LEVEL_ERROR) {
			return true;
		}
		if (threshold == LEVEL_DISABLED) {
			return true;
		}

		return false;
	}

	public void setThreshold(int threshold) {
		if (isValidThreshold(threshold)) {
			m_threshold = threshold;

			for (Logger logger : m_loggers.values()) {
				logger.setThreshold(threshold);
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
