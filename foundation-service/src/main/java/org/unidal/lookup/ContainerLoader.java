package org.unidal.lookup;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.unidal.lookup.extension.EnumComponentManagerFactory;

public class ContainerLoader {
   private static volatile DefaultPlexusContainer s_container;

   private static ConcurrentMap<Key, Object> m_components = new ConcurrentHashMap<Key, Object>();

   @SuppressWarnings("unchecked")
   static <T> T lookupById(Class<T> role, String roleHint, String id) throws ComponentLookupException {
      Key key = new Key(role, roleHint, id);
      Object component = m_components.get(key);

      if (component == null) {
         component = s_container.lookup(role, roleHint);

         if (m_components.putIfAbsent(key, component) != null) {
            component = m_components.get(key);
         }
      }

      return (T) component;
   }

   public static void destroyDefaultContainer() {
      if (s_container != null) {
         s_container.dispose();
         s_container = null;
      }
   }

   private static Class<?> findLoaderClass() {
      String loaderClassName = "com.site.lookup.ContainerLoader";
      Class<?> loaderClass = null;

      try {
         loaderClass = ContainerLoader.class.getClassLoader().loadClass(loaderClassName);
      } catch (ClassNotFoundException e) {
         // ignore it
      }

      try {
         loaderClass = Thread.currentThread().getContextClassLoader().loadClass(loaderClassName);
      } catch (ClassNotFoundException e) {
         // ignore it
      }

      return loaderClass;
   }

   // for back compatible
   private static DefaultPlexusContainer getContainerFromLookupLibrary(Class<?> loaderClass) {
      try {
         Field field = loaderClass.getDeclaredField("s_container");

         field.setAccessible(true);
         return (DefaultPlexusContainer) field.get(null);
      } catch (Exception e) {
         // ignore it
         e.printStackTrace();
      }

      return null;
   }

   public static PlexusContainer getDefaultContainer() {
      DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();

      configuration.setContainerConfiguration("/META-INF/plexus/plexus.xml");
      return getDefaultContainer(configuration);
   }

   public static PlexusContainer getDefaultContainer(ContainerConfiguration configuration) {
      if (s_container == null) {
         // Two ContainerLoaders should share the same PlexusContainer
         Class<?> loaderClass = findLoaderClass();

         synchronized (ContainerLoader.class) {
            if (loaderClass != null) {
               s_container = getContainerFromLookupLibrary(loaderClass);
            }

            if (s_container == null) {
               try {
                  preConstruction(configuration);

                  s_container = new DefaultPlexusContainer(configuration);

                  postConstruction(s_container);

                  if (loaderClass != null) {
                     setContainerToLookupLibrary(loaderClass, s_container);
                  }
               } catch (Exception e) {
                  throw new RuntimeException("Unable to create Plexus container.", e);
               }
            }
         }
      }

      return s_container;
   }

   private static void postConstruction(DefaultPlexusContainer container) {
      container.getComponentRegistry().registerComponentManagerFactory(new EnumComponentManagerFactory());
   }

   private static void preConstruction(ContainerConfiguration configuration) throws UndefinedLifecycleHandlerException {
      LifecycleHandler plexus = configuration.getLifecycleHandlerManager().getLifecycleHandler("plexus");

      plexus.addBeginSegment(new org.unidal.lookup.extension.PostConstructionPhase());
   }

   private static void setContainerToLookupLibrary(Class<?> loaderClass, PlexusContainer container) {
      try {
         Field field = loaderClass.getDeclaredField("s_container");

         field.setAccessible(true);
         field.set(null, container);
      } catch (Exception e) {
         // ignore it
         e.printStackTrace();
      }
   }

   static class Key {
      private Class<?> m_role;

      private String m_roleHint;

      private String m_id;

      public Key(Class<?> role, String roleHint, String id) {
         m_role = role;
         m_roleHint = roleHint == null ? "default" : roleHint;
         m_id = id;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Key) {
            Key e = (Key) obj;

            if (e.m_role != m_role) {
               return false;
            }

            if (!e.m_roleHint.equals(m_roleHint)) {
               return false;
            }

            if (!e.m_id.equals(m_id)) {
               return false;
            }

            return true;
         }

         return false;
      }

      @Override
      public int hashCode() {
         int hashCode = 0;

         hashCode = hashCode * 31 + m_role.hashCode();
         hashCode = hashCode * 31 + m_roleHint.hashCode();
         hashCode = hashCode * 31 + m_id.hashCode();

         return hashCode;
      }
   }
}
