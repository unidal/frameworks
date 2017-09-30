package org.unidal.lookup.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.ResourceMatcher;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

public class ComponentModelManager {
	private List<PlexusModel> m_models = new ArrayList<PlexusModel>();

	// for test purpose
	private PlexusModel m_model = new PlexusModel();

	private Map<ComponentKey, ComponentModel> m_cache = new HashMap<ComponentKey, ComponentModel>();

	public ComponentModelManager() {
		m_models.add(m_model);
	}

	public void addComponent(ComponentModel component) {
		m_model.addComponent(component);
	}

	public ComponentModel getComponentModel(ComponentKey key) {
		ComponentModel model = m_cache.get(key);
		boolean found = false;

		if (!found) {
			for (ComponentModel component : m_model.getComponents()) {
				if (key.matches(component.getRole(), component.getHint())) {
					model = component;
					found = true;
					m_cache.put(key, component);
					break;
				}
			}
		}

		if (!found) {
			for (PlexusModel plexus : m_models) {
				for (ComponentModel component : plexus.getComponents()) {
					if (key.matches(component.getRole(), component.getHint())) {
						model = component;
						found = true;
						m_cache.put(key, component);
						break;
					}
				}
				
				if (found) {
					break;
				}
			}
		}

		return model;
	}

	public List<String> getRoleHints(String role) {
		List<String> roleHints = new ArrayList<String>();
		Set<String> done = new HashSet<String>();

		for (PlexusModel model : m_models) {
			for (ComponentModel component : model.getComponents()) {
				if (role.equals(component.getRole())) {
					String roleHint = component.getHint();

					if (done.contains(roleHint)) {
						continue;
					} else {
						done.add(roleHint);
					}

					roleHints.add(roleHint);
				}
			}
		}

		return roleHints;
	}

	public boolean hasComponentModel(ComponentKey key) {
		return getComponentModel(key) != null;
	}

	private void loadCompoents(URL url) throws IOException, SAXException {
		// ignore internals components.xml files within official plexus-container-default.jar
		if (url.getPath().contains("/plexus-container-default/")) {
			return;
		}

		InputStream in = url.openStream();
		String xml = Files.forIO().readFrom(in, "utf-8");

		// to be compatible with plexus.xml
		if (xml != null && xml.contains("<component-set>")) {
			xml = xml.replace("<component-set>", "<plexus>");
			xml = xml.replace("</component-set>", "</plexus>");
		}

		try {
			PlexusModel model = DefaultSaxParser.parse(xml);

			m_models.add(model);
		} catch (SAXException e) {
			System.err.println(String.format("Bad plexus resource(%s): ", url) + xml);
			throw e;
		}
	}

	public void loadComponents(InputStream in) throws Exception {
		if (in != null) {
			try {
				PlexusModel model = DefaultSaxParser.parse(in);

				m_models.add(model);
			} finally {
				in.close();
			}
		}
	}

	public void loadComponentsFromClasspath() throws Exception {
		List<URL> urls = scanComponents();

		for (URL url : urls) {
			loadCompoents(url);
		}
	}

	public void reset() {
		m_model.getComponents().clear();
	}

	List<URL> scanComponents() throws IOException {
		final List<URL> components = new ArrayList<URL>();

		Scanners.forResource().scan("META-INF/plexus/", new ResourceMatcher() {
			@Override
			public Direction matches(URL url, String path) {
				if (!path.endsWith(".xml")) {
					return Direction.DOWN;
				}

				// ignore configuration from official plexus-container-default.jar
				if (path.contains("/plexus-container-default/")) {
					return Direction.NEXT;
				}

				if (path.equals("plexus.xml")) {
					components.add(url);
				} else if (path.equals("components.xml") || path.startsWith("components-")) {
					components.add(url);
				}

				return Direction.DOWN;
			}
		});

		return components;
	}

	public void setComponentModel(ComponentKey key, Class<?> clazz) {
		for (PlexusModel model : m_models) {
			ComponentModel component = new ComponentModel() //
			      .setRole(key.getRole()).setRoleHint(key.getRoleHint()).setImplementation(clazz.getName());

			model.addComponent(component);
		}
	}
}
