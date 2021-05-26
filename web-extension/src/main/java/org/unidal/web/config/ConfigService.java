package org.unidal.web.config;

import java.util.List;

public interface ConfigService {
	public String CATEGORY_CONFIG = "config";

	public String CATEGORY_SECURITY = "security";

	public List<String> findCategories() throws ConfigException;

	public Config findConfig(String category, String name) throws ConfigException;

	public List<Config> findConfigs(String category) throws ConfigException;

	public boolean getBoolean(String category, String name, boolean defaultValue);

	public String getString(String category, String name, String defaultValue);

	public int refreshCache();

	public void register(ConfigEventListener listener);

	public void updateConfig(String category, String name, String description, String config) throws ConfigException;
}
