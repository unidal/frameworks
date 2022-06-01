package org.unidal.lookup.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.unidal.helper.Files;
import org.unidal.lookup.container.model.PlexusModelHelper;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.xml.sax.SAXException;

public class ComponentModelManager {
   private List<PlexusModel> m_models = new ArrayList<PlexusModel>();

   // for test purpose
   private PlexusModel m_model = new PlexusModel();

   public ComponentModelManager(InputStream in) throws Exception {
      if (in != null) {
         PlexusModel model = PlexusModelHelper.fromXml(in);

         m_models.add(model);
      }

      loadPlexusModels("META-INF/plexus/plexus.xml");
      loadPlexusModels("META-INF/plexus/components.xml");
   }

   public ComponentModel getComponentModel(ComponentKey key) {
      for (ComponentModel component : m_model.getComponents()) {
         if (key.matches(component.getRole(), component.getRoleHint())) {
            return component;
         }
      }

      for (PlexusModel plexus : m_models) {
         for (ComponentModel component : plexus.getComponents()) {
            if (key.matches(component.getRole(), component.getRoleHint())) {
               return component;
            }
         }
      }

      return null;
   }

   public List<String> getRoleHints(String role) {
      List<String> roleHints = new ArrayList<String>();
      Set<String> done = new HashSet<String>();

      for (PlexusModel model : m_models) {
         for (ComponentModel component : model.getComponents()) {
            if (role.equals(component.getRole())) {
               String roleHint = component.getRoleHint();

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

   private void loadPlexusModels(String resource) throws IOException, SAXException {
      Enumeration<URL> urls = getClass().getClassLoader().getResources(resource);

      while (urls.hasMoreElements()) {
         URL url = urls.nextElement();

         // ignore plexus internal components.xml
         if (url.getPath().contains("/plexus-container-default/")) {
            continue;
         }

         InputStream in = url.openStream();
         String xml = Files.forIO().readFrom(in, "utf-8");
         PlexusModel model = PlexusModelHelper.fromXml(xml);

         m_models.add(model);
      }
   }

   public void setComponentModel(ComponentKey key, Class<?> clazz) {
      for (PlexusModel model : m_models) {
         ComponentModel component = new ComponentModel() //
               .setRole(key.getRole()).setRoleHint(key.getRoleHint()).setImplementation(clazz.getName());

         model.addComponent(component);
      }
   }

   public void addComponent(ComponentModel component) {
      m_model.addComponent(component);
   }

   public void reset() {
      m_model.getComponents().clear();
   }
}
