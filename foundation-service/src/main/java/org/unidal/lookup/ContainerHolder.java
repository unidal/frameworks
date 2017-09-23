package org.unidal.lookup;

import java.util.List;
import java.util.Map;

import org.unidal.lookup.extension.Contextualizable;

public abstract class ContainerHolder implements Contextualizable {
	private PlexusContainer m_container;

	public void contextualize(Map<String, Object> context) {
		m_container = (PlexusContainer) context.get("plexus");
	}

	protected PlexusContainer getContainer() {
		return m_container;
	}

	protected <T> boolean hasComponent(Class<T> role) {
		return hasComponent(role, null);
	}

	protected <T> boolean hasComponent(Class<T> role, String roleHint) {
		return getContainer().hasComponent(role, roleHint);
	}

	protected <T> T lookup(Class<T> role) throws LookupException {
		return lookup(role, null);
	}

	protected <T> T lookup(Class<T> role, String roleHint) throws LookupException {
		try {
			return (T) getContainer().lookup(role, roleHint == null ? "default" : roleHint);
		} catch (ComponentLookupException e) {
			String key = role.getName() + ":" + (roleHint == null ? "default" : roleHint);

			throw new LookupException("Unable to lookup component(" + key + ").", e);
		}
	}

	protected <T> List<T> lookupList(Class<T> role) throws LookupException {
		try {
			return (List<T>) getContainer().lookupList(role);
		} catch (ComponentLookupException e) {
			String key = role.getName();

			throw new LookupException("Unable to lookup component list(" + key + ").", e);
		}
	}

	protected <T> Map<String, T> lookupMap(Class<T> role) throws LookupException {
		try {
			return (Map<String, T>) getContainer().lookupMap(role);
		} catch (ComponentLookupException e) {
			String key = role.getName();

			throw new LookupException("Unable to lookup component map(" + key + ").", e);
		}
	}

	protected void release(Object component) throws LookupException {
		if (component != null) {
			getContainer().release(component);
		}
	}
}
