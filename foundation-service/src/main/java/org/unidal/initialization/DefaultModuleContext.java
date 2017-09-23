package org.unidal.initialization;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Contextualizable;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.logging.TimedConsoleLoggerManager;

@Named(type = ModuleContext.class)
public class DefaultModuleContext implements ModuleContext, Contextualizable {
	private PlexusContainer m_container;

	private Map<String, Object> m_attributes = new HashMap<String, Object>();

	private Logger m_logger;

	public DefaultModuleContext() {
	}

	public DefaultModuleContext(PlexusContainer container) {
		m_container = container;

		setup();
	}

	@Override
	public void contextualize(Map<String, Object> context) {
		m_container = (PlexusContainer) context.get("plexus");

		setup();
	}

	@Override
	public void error(String message) {
		m_logger.error(message);
	}

	@Override
	public void error(String message, Throwable e) {
		m_logger.error(message, e);
	}

	@Override
	public <T> T getAttribute(String name) {
		return getAttribute(name, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(String name, T defaultValue) {
		Object value = m_attributes.get(name);

		if (value != null) {
			return (T) value;
		} else {
			return defaultValue;
		}
	}

	public PlexusContainer getContainer() {
		return m_container;
	}

	@Override
	public Module[] getModules(String... names) {
		Module[] modules = new Module[names.length];
		int index = 0;

		for (String name : names) {
			modules[index++] = lookup(Module.class, name);
		}

		return modules;
	}

	@Override
	public void info(String message) {
		m_logger.info(message);
	}

	@Override
	public <T> T lookup(Class<T> role) {
		return lookup(role, null);
	}

	@Override
	public <T> T lookup(Class<T> role, String roleHint) {
		try {
			return m_container.lookup(role, roleHint);
		} catch (ComponentLookupException e) {
			throw new RuntimeException("Unable to get component: " + role + ".", e);
		}
	}

	@Override
	public void release(Object component) {
		m_container.release(component);
	}

	@Override
	public void setAttribute(String name, Object value) {
		m_attributes.put(name, value);
	}

	private void setup() {
		try {
			m_logger = m_container.getLogger();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get instance of Logger, please make sure " //
			      + "the environment was setup correctly!", e);
		}

		skipClassForLogger(getClass());
	}

	public void skipClassForLogger(Class<?> clazz) {
		TimedConsoleLoggerManager.skipClass(clazz);
	}

	@Override
	public void warn(String message) {
		m_logger.warn(message);
	}
}
