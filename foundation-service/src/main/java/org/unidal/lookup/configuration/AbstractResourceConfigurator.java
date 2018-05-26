package org.unidal.lookup.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;
import org.unidal.tuple.Pair;

public abstract class AbstractResourceConfigurator implements Configurator {
   protected static final String PER_LOOKUP = "per-lookup";

   protected static final String ENUM = "enum";

   protected static <T> Component A(Class<T> clazz) {
      return A(clazz, null);
   }

   @SuppressWarnings("unchecked")
   protected static <T> Component A(Class<T> clazz, String roleHint) {
      Named named = clazz.getAnnotation(Named.class);
      Class<?> role = null;

      if (named == null) {
         String legacyMode = System.getProperty("LegacyMode");

         if ("true".equals(legacyMode)) {
            if (clazz.getSimpleName().equals("Handler") || clazz.getSimpleName().equals("JspViewer")) {
               role = Named.Default.class;
            }
         }
         
         if (role == null) {
            throw new IllegalStateException(
                  String.format("Class(%s) is not annotated by %s.", clazz.getName(), Named.class.getName()));
         }
      } else {
         role = named.type();
      }
      
      if (role == Named.Default.class) {
         role = clazz;
      } else {
         if (!role.isAssignableFrom(clazz)) {
            throw new IllegalStateException(String.format("Class(%s) is not assignable from class(%s).", role.getName(),
                  clazz, clazz.getName(), role.getName()));
         }
      }

      if (roleHint == null && named != null) {
         roleHint = named.value();
      }

      if (roleHint != null && roleHint.length() == 0) {
         roleHint = null;
      }

      Component component = new Component((Class<Object>) role, roleHint, clazz);

      if (roleHint != null) {
         if (clazz.isEnum()) {
            component.is(ENUM);
         }
      }

      if (named != null && named.instantiationStrategy().length() > 0) {
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
         Class<?> elementType = getElementType(field);

         if (role == Inject.Default.class) {
            role = elementType;
         }

         if (type == elementType) { // normal simple case
            List<Pair<Object, String>> require = findOrCreateList(requires, role);

            if (roleHints.length == 0) {
               require.add(new Pair<Object, String>("default", field.getName()));
            } else if (roleHints.length == 1) {
               require.add(new Pair<Object, String>(roleHints[0], field.getName()));
            } else {
               require.add(new Pair<Object, String>(roleHints, field.getName()));
            }
         } else { // List, Set or Array
            List<Pair<Object, String>> require = findOrCreateList(requires, role);

            if (roleHints.length == 0) {
               require.add(new Pair<Object, String>(new String[0], field.getName()));
            } else {
               require.add(new Pair<Object, String>(roleHints, field.getName()));
            }
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

   private static void collectFields(Class<?> clazz, Map<Class<?>, List<Pair<Object, String>>> requires,
         Map<String, String> attributes) {
      List<Field> fields = Reflects.forField().getDeclaredFields(clazz, new IMemberFilter<Field>() {
         @Override
         public boolean filter(Field member) {
            return member.isAnnotationPresent(Inject.class) || member.isAnnotationPresent(InjectAttribute.class);
         }
      });

      Collections.sort(fields, new Comparator<Field>() {
         @Override
         public int compare(Field o1, Field o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });

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

   private static <S, T> List<T> findOrCreateList(Map<S, List<T>> map, S key) {
      List<T> list = map.get(key);

      if (list == null) {
         list = new ArrayList<T>(3);
         map.put(key, list);
      }

      return list;
   }

   protected static void generatePlexusComponentsXmlFile(AbstractResourceConfigurator rc) {
      File file = rc.getConfigurationFile();

      try {
         rc.saveToFile(file);

         System.out.println(String.format("File %s generated. File length is %s.", file, file.length()));
      } catch (IOException e) {
         System.err.println(String.format("Error when generating %s file.", file));
         e.printStackTrace();
      }
   }

   private static Class<?> getElementType(Field field) {
      Type type = field.getGenericType();
      Class<?> clazz = field.getType();

      if (clazz.isArray()) {
         return clazz.getComponentType();
      } else {
         if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();

            if (clazz == List.class || clazz == Set.class || clazz == Collection.class) {
               if (args.length == 1) {
                  return (Class<?>) args[0];
               }
            } else if (clazz == Map.class) {
               if (args.length == 2) {
                  return (Class<?>) args[1];
               }
            }
         }
      }

      return field.getType();
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

   protected File getConfigurationFile() {
      File baseDir = getBaseDir();
      Class<?> testClass = getTestClass();
      File file;

      if (testClass != null) {
         file = new File(baseDir, String.format("src/test/resources/%s.xml", testClass.getName().replace('.', '/')));
      } else if (isMavenPlugin()) {
         file = new File(baseDir, "src/main/resources/META-INF/plexus/components.xml");
      } else {
         String projectName = baseDir.getName();
         int pos = projectName.indexOf('.');

         if (pos > 0) {
            projectName = projectName.substring(0, pos);
         }

         file = new File(baseDir, String.format("src/main/resources/META-INF/plexus/components-%s.xml", projectName));
      }

      try {
         return file.getCanonicalFile();
      } catch (IOException e) {
         return file;
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

   protected boolean isMavenPlugin() {
      return false;
   }

   protected String property(String name, String defaultValue) {
      return System.getProperty(name, defaultValue);
   }

   protected void saveToFile(File file) throws IOException {
      // create parent directory if not there
      File parent = file.getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      String content = Configurators.forPlexus().generateXmlConfiguration(this, defineComponents());

      if (!isMavenPlugin() && getTestClass() == null) {
         File oldFile = new File(parent, "components.xml");

         if (oldFile.exists()) {
            oldFile.delete();
         }
      }

      Files.forIO().writeTo(file, content);
   }
}
