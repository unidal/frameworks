package org.unidal.lookup.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;
import org.unidal.tuple.Pair;

public abstract class AbstractResourceConfigurator {
   protected static final String PER_LOOKUP = "per-lookup";

   protected static final String ENUM = "enum";

   protected static <T> Component A(Class<T> clazz) {
      return A(clazz, null);
   }

   @SuppressWarnings("unchecked")
   protected static <T> Component A(Class<T> clazz, String enumField) {
      Named named = clazz.getAnnotation(Named.class);

      if (named == null) {
         throw new IllegalStateException(String.format("Class(%s) is not annotated by %s.", clazz.getName(),
               Named.class.getName()));
      }

      Class<?> role = named.type();
      String roleHint = named.value();

      if (role == Named.Default.class) {
         role = clazz;
      } else {
         if (!role.isAssignableFrom(clazz)) {
            throw new IllegalStateException(String.format("Class(%s) is not assignable from class(%s).",
                  role.getName(), clazz, clazz.getName(), role.getName()));
         }
      }

      if (enumField != null) {
         roleHint = enumField;
      } else if (roleHint.length() == 0) {
         roleHint = null;
      }

      Component component = new Component((Class<Object>) role, roleHint, clazz);

      if (enumField != null) {
         if (clazz.isEnum()) {
            component.is(ENUM);
         }
      } else if (named.instantiationStrategy().length() > 0) {
         component.is(named.instantiationStrategy());
      }

      Map<Class<?>, List<Pair<Object, String>>> requires = new LinkedHashMap<Class<?>, List<Pair<Object, String>>>();
      Map<String, String> attributes = new LinkedHashMap<String, String>();

      collectFields(clazz, requires, attributes);

      for (Map.Entry<Class<?>, List<Pair<Object, String>>> e : requires.entrySet()) {
         List<Pair<Object, String>> list = e.getValue();

         for (Pair<Object, String> item : list) {
            Object key = item.getKey();

            if (key instanceof String[]) {
               String[] roleHints = (String[]) key;

               if (roleHints.length == 1) {
                  if (list.size() == 1) {
                     component.req(e.getKey(), roleHints[0]);
                  } else {
                     component.req(e.getKey(), roleHints[0], item.getValue());
                  }
               } else {
                  component.req(e.getKey(), roleHints, item.getValue());
               }
            } else {
               if (list.size() == 1) {
                  component.req(e.getKey(), (String) key);
               } else {
                  component.req(e.getKey(), (String) key, item.getValue());
               }
            }
         }
      }

      for (Map.Entry<String, String> attribute : attributes.entrySet()) {
         component.config(new Configuration(attribute.getKey()).value(attribute.getValue()));
      }

      return component;
   }

   protected static <T> Component C(Class<T> role) {
      return new Component(role);
   }

   protected static <T> Component C(Class<T> role, Class<? extends T> implementationClass) {
      return new Component(role, implementationClass);
   }

   protected static <T> Component C(Class<T> role, Object roleHint, Class<? extends T> implementationClass) {
      return new Component(role, roleHint, implementationClass);
   }

   private static void collectField(Class<?> clazz, Field field, Map<Class<?>, List<Pair<Object, String>>> requires,
         Map<String, String> attributes) {
      Inject inject = field.getAnnotation(Inject.class);
      InjectAttribute injectAttribute = field.getAnnotation(InjectAttribute.class);

      if (inject != null && injectAttribute != null) {
         throw new IllegalStateException(String.format("Field(%s) can't be annotated by both %s and %s.",
               field.getName(), Inject.class.getName(), InjectAttribute.class.getName()));
      }

      if (inject != null) {
         Class<?> role = inject.type();
         String[] roleHints = inject.value();
         Class<?> type = field.getType();

         if (role != Inject.Default.class) {
            if (roleHints.length <= 1 && !role.isAssignableFrom(type)) {
               throw new IllegalStateException(String.format("Field(%s) of %s can only be injected " + //
                     "by subclass of %s instead of %s.", field.getName(), clazz, role.getName(), type.getName()));
            }
         } else {
            if (roleHints.length <= 1) {
               role = field.getType();
            } else {
               role = getElementType(field.getGenericType(), field);
            }
         }

         if (roleHints.length <= 1) {
            List<Pair<Object, String>> require = requires.get(role);

            if (require == null) {
               require = new ArrayList<Pair<Object, String>>(3);
               requires.put(role, require);
            }

            if (roleHints.length == 0) {
               require.add(new Pair<Object, String>("default", field.getName()));
            } else if (roleHints.length == 1) {
               require.add(new Pair<Object, String>(roleHints[0], field.getName()));
            }
         } else {
            List<Pair<Object, String>> require = requires.get(role);

            if (require == null) {
               require = new ArrayList<Pair<Object, String>>(3);
               requires.put(role, require);
            }

            require.add(new Pair<Object, String>(roleHints, field.getName()));
         }
      }

      if (injectAttribute != null) {
         String value = injectAttribute.value();

         if (!value.equals(InjectAttribute.DEFAULT)) {
            String name = field.getName();

            if (name.startsWith("m_")) {
               name = name.substring(2);
            }

            attributes.put(name, value);
         }
      }
   }

   private static Class<?> getElementType(Type type, Field field) {
      if (type instanceof ParameterizedType) {
         return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
      }

      throw new IllegalStateException(String.format("Unsupported type(%s) of field(%s) of %s, use List instead!", type,
            field.getName(), field.getDeclaringClass()));
   }

   private static void collectFields(Class<?> clazz, Map<Class<?>, List<Pair<Object, String>>> requires,
         Map<String, String> attributes) {
      Field[] fields = clazz.getDeclaredFields();

      for (Field field : fields) {
         collectField(clazz, field, requires, attributes);
      }

      Class<?> superClass = clazz.getSuperclass();

      if (superClass != null) {
         collectFields(superClass, requires, attributes);
      }
   }

   protected static Configuration E(String name) {
      return new Configuration(name);
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

   public abstract List<Component> defineComponents();

   protected File getConfigurationFile() {
      File baseDir = getBaseDir();
      Class<?> testClass = getTestClass();

      if (testClass != null) {
         return new File(baseDir, String.format("src/test/resources/%s.xml", testClass.getName().replace('.', '/')));
      } else {
         return new File(baseDir, "src/main/resources/META-INF/plexus/components.xml");
      }
   }

   protected File getBaseDir() {
      URL url = getClass().getResource(getClass().getSimpleName() + ".class");
      String path = url.getPath();
      int pos = path.indexOf("/target/classes");

      if (pos < 0) {
         pos = path.indexOf("/target/test-classes");
      }

      if (pos > 0) {
         return new File(path.substring(0, pos));
      } else {
         return new File(".");
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
