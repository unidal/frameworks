package org.unidal.lookup.container;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.unidal.lookup.container.model.entity.ComponentModel;

public class MyPlexusContainer implements PlexusContainer {
	private ComponentManager m_manager;

	private Context m_context;

	public MyPlexusContainer() throws Exception {
		this(null);
	}

	public MyPlexusContainer(InputStream in) throws Exception {
		m_manager = new ComponentManager(this, in);
		m_context = new MyPlexusContainerContext(this);
	}

	@Override
	public <T> void addComponent(T component, Class<?> role, String roleHint) {
		m_manager.register(new ComponentKey(role, roleHint), component);
	}

	@Override
	public void addComponentModel(Object component) {
		m_manager.addComponentModel((ComponentModel) component);
	}

	@Override
	public void addContextValue(Object key, Object value) {
		m_context.put(key, value);
	}

	@Override
	public void dispose() {
		m_manager.destroy();
	}

	@Override
	public Context getContext() {
		return m_context;
	}

	@Override
	public boolean hasComponent(Class<?> type) {
		return hasComponent(type.getName(), null);
	}

	@Override
	public boolean hasComponent(Class<?> role, String roleHint) {
		return m_manager.hasComponent(new ComponentKey(role, roleHint));
	}

	public boolean hasComponent(String role) {
		return hasComponent(role, null);
	}

	public boolean hasComponent(String role, String roleHint) {
		return m_manager.hasComponent(new ComponentKey(role, roleHint));
	}

	@Override
	public <T> T lookup(Class<T> type) throws ComponentLookupException {
		return m_manager.lookup(new ComponentKey(type, null));
	}

	@Override
	public <T> T lookup(Class<T> type, String roleHint) throws ComponentLookupException {
		return m_manager.lookup(new ComponentKey(type, roleHint));
	}

	@Override
	public <T> List<T> lookupList(Class<T> type) throws ComponentLookupException {
		return m_manager.lookupList(type.getName());
	}

	@Override
	public <T> List<T> lookupList(Class<T> type, List<String> roleHints) throws ComponentLookupException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Map<String, T> lookupMap(Class<T> type) throws ComponentLookupException {
		return m_manager.lookupMap(type.getName());
	}

	@Override
	public <T> Map<String, T> lookupMap(Class<T> type, List<String> roleHints) throws ComponentLookupException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void release(Object component) {
		m_manager.release(component);
	}
}
