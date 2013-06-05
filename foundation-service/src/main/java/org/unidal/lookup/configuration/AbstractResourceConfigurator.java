package org.unidal.lookup.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

public abstract class AbstractResourceConfigurator {
	protected static final String PER_LOOKUP = "per-lookup";
	
	protected static final String ENUM = "enum";

	protected static <T> Component C(Class<T> role) {
		return new Component(role);
	}

	protected static <T> Component C(Class<T> role, Class<? extends T> implementationClass) {
		return new Component(role, implementationClass);
	}

	protected static <T> Component C(Class<T> role, Object roleHint, Class<? extends T> implementationClass) {
		return new Component(role, roleHint, implementationClass);
	}

	protected static Configuration E(String name, String... attributePairs) {
		return new Configuration(name, attributePairs);
	}

	protected static void generatePlexusComponentsXmlFile(AbstractResourceConfigurator rc) {
		File file = rc.getConfigurationFile();

		try {
			rc.saveToFile();

			System.out.println(String.format("File %s generated. File length is %s.", file.getCanonicalPath(),
			      file.length()));
		} catch (IOException e) {
			System.err.println(String.format("Error when generating %s file.", file));
			e.printStackTrace();
		}
	}

	protected List<Component> defineComponent(Class<?> role) {
		return defineComponent(role, C(role));
	}

	private List<Component> defineComponent(Class<?> role, Component component) {
		List<Component> all = new ArrayList<Component>();
		Map<Class<?>, Component> map = new LinkedHashMap<Class<?>, Component>();

		map.put(role, component);
		processInjectFields(map, role, component);

		all.addAll(map.values());
		return all;
	}

	protected <T> List<Component> defineComponent(Class<T> role, Class<? extends T> implementationClass) {
		return defineComponent(role, C(role, implementationClass));
	}

	protected <T> List<Component> defineComponent(Class<T> role, Object roleHint, Class<? extends T> implementationClass) {
		return defineComponent(role, C(role, roleHint, implementationClass));
	}

	private void defineComponentRequirements(Map<Class<?>, Component> map, Class<?> clazz) {
		if (!map.containsKey(clazz)) {
			Component component = C(clazz);

			map.put(clazz, component);
			processInjectFields(map, clazz, component);
		}
	}

	public abstract List<Component> defineComponents();

	protected File getConfigurationFile() {
		Class<?> testClass = getTestClass();

		if (testClass != null) {
			return new File(String.format("src/test/resources/%s.xml", testClass.getName().replace('.', '/')));
		} else {
			return new File("src/main/resources/META-INF/plexus/components.xml");
		}
	}

	/**
	 * @return null means not a test class
	 */
	protected Class<?> getTestClass() {
		return null;
	}

	protected boolean isEnv(String name) {
		String env = System.getProperty("env");

		if (env != null && env.equals(name)) {
			return false;
		} else {
			return false;
		}
	}

	private void processInjectFields(Map<Class<?>, Component> map, Class<?> clazz, Component component) {
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);

			if (inject != null) {
				Class<?> role = inject.type();
				String alias = inject.value();
				Class<?> type = field.getType();

				if (role != Inject.Default.class) {
					if (!type.isAssignableFrom(role)) {
						throw new RuntimeException(String.format("Field %s of %s can only be injected " + //
						      "by subclass of %s instead of %s.", field.getName(), clazz, type.getName(), role.getName()));
					}
				}

				if (alias.length() == 0) {
					component.req(type);

					if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
						defineComponentRequirements(map, type);
					}
				} else {
					component.req(type, alias);
				}
			}
		}

		Class<?> superClass = clazz.getSuperclass();

		if (superClass != null) {
			processInjectFields(map, superClass, component);
		}
	}

	protected String property(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	protected void saveToFile() throws IOException {
		File file = getConfigurationFile();

		// create parent directory if not there
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		String content = Configurators.forPlexus().generateXmlConfiguration(defineComponents());

		Files.forIO().writeTo(file, content);
	}

}
