package org.unidal.lookup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.unidal.helper.Reflects;
import org.unidal.lookup.extension.EnumComponentManagerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ContainerLoader {
   private static volatile DefaultPlexusContainer s_container;

   private static ConcurrentMap<Key, Object> m_components = new ConcurrentHashMap<Key, Object>();

   public static void destroyDefaultContainer() {
      if (s_container != null) {
         m_components.clear();
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

   private static void postConstruction(DefaultPlexusContainer container) {
      container.getComponentRegistry().registerComponentManagerFactory(new EnumComponentManagerFactory());
   }

   @SuppressWarnings("unchecked")
   private static void preConstruction(ContainerConfiguration configuration) throws Exception {
      LifecycleHandler plexus = configuration.getLifecycleHandlerManager().getLifecycleHandler("plexus");
      Field field = Reflects.forField().getDeclaredField(AbstractLifecycleHandler.class, "beginSegment");

      field.setAccessible(true);

      List<Phase> segment = (List<Phase>) field.get(plexus);

      segment.add(0, new org.unidal.lookup.extension.PostConstructionPhase());

      try {
         new ContainerConfigurationDecorator().process(configuration);
      } catch (Exception e) {
         e.printStackTrace();
      }
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

   static class ContainerConfigurationDecorator {
      private String m_defaultPath = "META-INF/plexus/plexus.xml";

      private void fillFrom(Document to, DocumentBuilder builder, URL url) throws Exception {
         InputStream in = url.openStream();
         Document from = builder.parse(in);

         in.close();

         Node source = from.getDocumentElement().getElementsByTagName("components").item(0);
         Node target = to.getDocumentElement().getFirstChild();
         NodeList list = source.getChildNodes();
         int len = list.getLength();

         for (int i = 0; i < len; i++) {
            target.appendChild(to.importNode(list.item(i), true));
         }
      }

      public void process(ContainerConfiguration configuration) throws Exception {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         Document doc = builder.newDocument();
         String path = configuration.getContainerConfiguration();
         ClassRealm realm = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader())
               .getRealm("plexus.core");
         Enumeration<URL> resources = realm.getResources(m_defaultPath);
         Element root = doc.createElement("plexus");

         root.appendChild(doc.createElement("components"));
         doc.appendChild(root);
         doc.setXmlStandalone(true);

         if (path != null && !path.endsWith(m_defaultPath)) {
            URL url = realm.getResource(path);

            if (url != null) {
               fillFrom(doc, builder, url);
            }
         }

         for (URL url : Collections.list(resources)) {
            fillFrom(doc, builder, url);
         }

         if (doc.getDocumentElement().hasChildNodes()) {
            // Use a Transformer for output
            TransformerFactory transforerFactory = TransformerFactory.newInstance();
            Transformer transformer = transforerFactory.newTransformer();
            File tmp = File.createTempFile("plexus-", ".xml");
            StreamResult result = new StreamResult(new FileOutputStream(tmp));

            tmp.deleteOnExit();
            transformer.transform(new DOMSource(doc), result);

            configuration.setContainerConfigurationURL(tmp.toURI().toURL());
         }
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
