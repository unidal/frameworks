package org.unidal.web.mvc.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import com.site.helper.Reflects;

public class ModuleRegistry {
	private List<Class<?>> m_moduleClasses = new ArrayList<Class<?>>();

	private Class<?> m_defaultModuleClass;

	public Class<?> getDefaultModuleClass() {
		return m_defaultModuleClass;
	}

	public List<Class<?>> getModuleClasses() {
		return m_moduleClasses;
	}

	public void setModules(PlexusConfiguration configuration) {
		for (PlexusConfiguration child : configuration.getChildren()) {
			String moduleClassName = child.getValue("");
			Class<?> moduleClass = Reflects.forClass().getClass(moduleClassName);

			if (moduleClass != null) {
				String value = child.getAttribute("default", null);

				m_moduleClasses.add(moduleClass);

				if ("true".equals(value)) {
					m_defaultModuleClass = moduleClass;
				}
			}
		}
	}
}
