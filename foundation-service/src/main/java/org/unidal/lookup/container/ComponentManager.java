package org.unidal.lookup.container;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.container.lifecycle.ComponentLifecycle;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.logging.LoggerManager;

public class ComponentManager {
	// component cache
	// role => map (role hint => component)
	private Map<String, ComponentBox<?>> m_components = new HashMap<String, ComponentBox<?>>();

	private PlexusContainer m_container;

	private ComponentLifecycle m_lifecycle;

	private ComponentModelManager m_modelManager;

	private LoggerManager m_loggerManager;

	public ComponentManager(PlexusContainer container, InputStream in) throws Exception {
		m_container = container;
		m_modelManager = new ComponentModelManager();
		m_lifecycle = new ComponentLifecycle(this);

		if (in != null) {
			m_modelManager.loadComponents(in);
		}

		m_modelManager.loadComponentsFromClasspath();

		// keep it at last
		m_loggerManager = lookup(new ComponentKey(LoggerManager.class, null));

		register(new ComponentKey(PlexusContainer.class, null), container);
		register(new ComponentKey(Logger.class, null), m_loggerManager.getLoggerForComponent(""));
	}

	public void addComponentModel(ComponentModel component) {
		m_modelManager.addComponent(component);
	}

	public void destroy() {
		for (ComponentBox<?> box : m_components.values()) {
			box.destroy();
		}

		m_components.clear();
		m_modelManager.reset();
	}

	public PlexusContainer getContainer() {
		return m_container;
	}

	public LoggerManager getLoggerManager() {
		return m_loggerManager;
	}

	public boolean hasComponent(ComponentKey key) {
		return m_modelManager.hasComponentModel(key);
	}

	public void log(String pattern, Object... args) {
		if ("true".equals(m_container.getContext().get("verbose"))) {
			Logger logger = m_loggerManager.getLoggerForComponent(null);

			logger.info(String.format(pattern, args));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T lookup(ComponentKey key) throws ComponentLookupException {
		String role = key.getRole();
		ComponentBox<?> box = m_components.get(role);

		if (box == null) {
			box = new ComponentBox<T>(m_lifecycle);
			m_components.put(role, box);
		}

		ComponentModel model = m_modelManager.getComponentModel(key);

		if (model != null) {
			return (T) box.lookup(model);
		} else {
			throw new ComponentLookupException("No component defined!", role, key.getRoleHint());
		}
	}

	public <T> List<T> lookupList(String role) throws ComponentLookupException {
		List<String> roleHints = m_modelManager.getRoleHints(role);
		List<T> components = new ArrayList<T>();

		for (String roleHint : roleHints) {
			T component = lookup(new ComponentKey(role, roleHint));

			components.add(component);
		}

		return components;
	}

	public <T> Map<String, T> lookupMap(String role) throws ComponentLookupException {
		List<String> roleHints = m_modelManager.getRoleHints(role);
		Map<String, T> components = new LinkedHashMap<String, T>();

		for (String roleHint : roleHints) {
			T component = lookup(new ComponentKey(role, roleHint));

			components.put(roleHint, component);
		}

		return components;
	}

	public void register(ComponentKey key, Object component) {
		ComponentBox<Object> box = new ComponentBox<Object>(m_lifecycle).register(key, component);

		m_components.put(key.getRole(), box);
		m_modelManager.setComponentModel(key, component.getClass());
	}

	public void release(Object component) {
		m_lifecycle.stop(component);
	}
}
