package org.unidal.lookup.configuration;

import java.util.List;

import org.unidal.lookup.container.model.entity.PlexusModel;

public class Configurators {
   private Configurators() {
   }

   public static final Configurators forPlexus() {
      return new Configurators();
   }

   public String generateXmlConfiguration(List<Component> components) {
      PlexusModel model = new PlexusModel();

      for (Component component : components) {
         model.addComponent(component.getModel());
      }

      return model.toString();
   }
}
