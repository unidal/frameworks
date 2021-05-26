package org.unidal.web.config;

public interface ConfigEventListener {
	public void onEvent(ConfigEvent event) throws ConfigException;
}
