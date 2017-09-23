package org.unidal.lookup.logging;

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

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLogger implements Logger {
	private int m_threshold;

	private String m_name;

	public AbstractLogger(int threshold, String name) {
		if (!isValidThreshold(threshold)) {
			throw new IllegalArgumentException("Threshold " + threshold + " is not valid");
		}

		m_threshold = threshold;
		m_name = name;
	}

	public void debug(String message) {
		debug(message, null);
	}

	public void error(String message) {
		error(message, null);
	}

	@Override
	public String getName() {
		return m_name;
	}

	public int getThreshold() {
		return m_threshold;
	}

	public void info(String message) {
		info(message, null);
	}

	public boolean isDebugEnabled() {
		return m_threshold <= LEVEL_DEBUG;
	}

	public boolean isErrorEnabled() {
		return m_threshold <= LEVEL_ERROR;
	}

	public boolean isInfoEnabled() {
		return m_threshold <= LEVEL_INFO;
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

	public boolean isWarnEnabled() {
		return m_threshold <= LEVEL_WARN;
	}

	public void setThreshold(int threshold) {
		m_threshold = threshold;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public void warn(String message) {
		warn(message, null);
	}
}
