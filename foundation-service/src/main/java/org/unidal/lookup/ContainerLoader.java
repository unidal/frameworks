package org.unidal.lookup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.unidal.lookup.container.MyPlexusContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ContainerLoader {
   private static volatile PlexusContainer s_container;

   public static void destroy() {
      if (s_container != null) {
         s_container.dispose();
         s_container = null;
      }
   }

   public static PlexusContainer getDefaultContainer() {
      return getDefaultContainer(null);
   }

   public static PlexusContainer getDefaultContainer(ContainerConfiguration configuration) {
      if (s_container == null) {
         try {
            if (configuration != null) {
               String configure = configuration.getContainerConfiguration();
               InputStream in = ContainerLoader.class.getClassLoader().getResourceAsStream(configure);

               s_container = new MyPlexusContainer(in);
            } else {
               s_container = new MyPlexusContainer();
            }
         } catch (Exception e) {
            throw new RuntimeException("Unable to create Plexus container!", e);
         }
      }

      return s_container;
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
