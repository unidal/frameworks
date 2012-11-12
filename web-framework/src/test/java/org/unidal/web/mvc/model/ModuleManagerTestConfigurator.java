package org.unidal.web.mvc.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.unidal.web.configuration.AbstractWebComponentsConfigurator;
import org.unidal.web.test.book.BookModule;

import org.unidal.lookup.configuration.Component;

public class ModuleManagerTestConfigurator extends AbstractWebComponentsConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ModuleManagerTestConfigurator());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, null, BookModule.class);

		return all;
	}

	@Override
	protected File getConfigurationFile() {
		return new File("src/test/resources/" + ModuleManagerTest.class.getName().replace('.', '/') + ".xml");
	}
}
