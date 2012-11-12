package org.unidal.lookup.configuration;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.ReaderFactory;

public abstract class AbstractScriptConfiguration implements Initializable {
   private String m_projectName;

   private String m_config;

   private Properties m_properties;

   private PlexusConfiguration m_configuration;

   protected AbstractScriptConfiguration(String projectName) {
      m_projectName = projectName;
   }

   protected static void generateScriptFile(AbstractScriptConfiguration configuration, File script, boolean isUnix) {
      try {
         configuration.setConfig("config.xml");
         configuration.initialize();

         String content = configuration.generateScript(isUnix);

         script.getParentFile().mkdirs();
         FileUtils.fileWrite(script.getPath(), content);
         System.out.println("File " + script.getCanonicalPath() + " generated. File length is " + script.length());
      } catch (Exception e) {
         System.err.println("Error when generating " + script + " file.");
         e.printStackTrace();
      }
   }

   private String generateScript(boolean isUnix) throws InvocationTargetException, IllegalArgumentException,
         IllegalAccessException {
      Map<Property, Method> map = getPropertyMap();
      StringBuilder sb = new StringBuilder(2048);
      StringBuilder options = new StringBuilder(256);
      String prefix = (isUnix ? "# " : "REM ");
      String lf = (isUnix ? "\n" : "\r\n");

      if (!isUnix) {
         sb.append("@echo off").append(lf);
      }

      sb.append(prefix).append("JDK version 1.5 is required. However, it does not work on JDK 1.6.").append(lf);
      sb.append(prefix).append("Following system properties are supported by this robot:").append(lf);

      for (Map.Entry<Property, Method> e : map.entrySet()) {
         String name = e.getKey().name();
         String desc = e.getKey().desc();
         Object value = e.getValue().invoke(this, new Object[0]);

         sb.append(prefix).append("   -D").append(pad(name, 20)).append(desc);
         sb.append(" Default value is \"").append(value).append("\".");
         sb.append(lf);

         if (e.getKey().required()) {
            options.append(" -D").append(name).append('=').append(value);
         }
      }

      sb.append(lf);
      sb.append("java").append(options).append(" -jar ").append(m_projectName).append(".jar").append(lf);

      return sb.toString();
   }

   protected String getParameterValue(String property) {
      String value = m_properties.getProperty(property);

      if (value == null) {
         PlexusConfiguration parameters = m_configuration.getChild("parameters");
         PlexusConfiguration parameter = parameters.getChild(property);
         PlexusConfiguration defaultValue = parameter.getChild("default-value");

         value = defaultValue.getValue(null);

         if (value == null) {
            throw new IllegalArgumentException("Parameter(" + property + ") should be defined at " + m_config
                  + " or be passed in from command line");
         }
      }

      return value;
   }

   protected int getParameterValue(String property, int defaultValue) {
      try {
         return Integer.parseInt(getParameterValue(property));
      } catch (Exception e) {
         return defaultValue;
      }
   }

   protected boolean getParameterValue(String property, boolean defaultValue) {
      try {
         return Boolean.getBoolean(getParameterValue(property));
      } catch (Exception e) {
         return defaultValue;
      }
   }

   protected String getParameterValue(String property, String defaultValue) {
      try {
         return getParameterValue(property);
      } catch (Exception e) {
         return defaultValue;
      }
   }

   private Map<Property, Method> getPropertyMap() {
      Map<Property, Method> map = new LinkedHashMap<Property, Method>();
      List<Method> allMethods = new ArrayList<Method>();
      Class<?> clazz = getClass();

      while (clazz != Object.class) {
         Method[] methods = clazz.getDeclaredMethods();

         for (Method method : methods) {
            allMethods.add(method);
         }

         clazz = clazz.getSuperclass();
      }

      for (Method method : allMethods) {
         Property property = method.getAnnotation(Property.class);

         if (property != null) {
            method.setAccessible(true);
            map.put(property, method);
         }
      }

      return map;
   }

   public void initialize() throws InitializationException {
      Reader reader;

      try {
         String config = System.getProperty("config");

         if (config != null) {
            m_config = config;
         }

         if (new File(m_config).canRead()) {
            reader = ReaderFactory.newXmlReader(new File(m_config));
         } else {
            InputStream is = getClass().getClassLoader().getResourceAsStream(m_config);

            if (is != null) {
               reader = ReaderFactory.newXmlReader(is);
            } else {
               throw new InitializationException(m_config + " can't be found at current directory or as resource");
            }
         }

         m_configuration = PlexusTools.buildConfiguration(m_config, reader);
         reader.close();
      } catch (Exception e) {
         throw new InitializationException("Error when loading XML configuration " + m_config, e);
      }

      m_properties = System.getProperties();
   }

   private String pad(String str, int maxLen) {
      StringBuilder sb = new StringBuilder(maxLen);

      sb.append(str);

      for (int i = str.length(); i < maxLen; i++) {
         sb.append(' ');
      }

      return sb.toString();
   }

   public void setConfig(String config) {
      m_config = System.getProperty("config", config);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   public @interface Property {
      String desc();

      String name();

      boolean required() default false;
   }
}
