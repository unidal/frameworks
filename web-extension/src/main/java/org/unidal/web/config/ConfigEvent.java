package org.unidal.web.config;

public class ConfigEvent {
	private Config m_config;

	private boolean m_localOnly;

	public ConfigEvent(Config config) {
		m_config = config;
	}

	public ConfigEvent(Config config, boolean localOnly) {
		m_config = config;
		m_localOnly = localOnly;
	}

	public Config getConfig() {
		return m_config;
	}

	public boolean isEligible(String category) {
		if (!m_config.getCategory().equals(category)) {
			return false;
		}

		return true;
	}

	public boolean isEligible(String category, String name) {
		if (!m_config.getCategory().equals(category)) {
			return false;
		}

		if (!m_config.getName().equals(name)) {
			return false;
		}

		return true;
	}

	public boolean isLocalOnly() {
		return m_localOnly;
	}
}
