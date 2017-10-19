package org.unidal.lookup.configuration;

import java.util.List;

import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.transform.DefaultXmlBuilder;

public class Configurators {
   private Configurators() {
   }

   public static final Configurators forPlexus() {
      return new Configurators();
   }

   public String generateXmlConfiguration(Configurator configurator, List<Component> components) {
      PlexusModel model = new PlexusModel();

      for (Component component : components) {
         model.addComponent(component.getModel());
      }

      StringBuilder sb = new StringBuilder(4096);
      String xml = new DefaultXmlBuilder(false, sb).buildXml(model);
      String comment = String.format("<!-- THIS FILE WAS AUTO GENERATED FROM class %s, DO NOT EDIT IT -->",
            configurator.getClass().getName());

      xml = prependComment(xml, comment);

      return xml.replaceAll("   ", "\t").replaceAll("\r\n", "\n");
   }

   private String prependComment(String xml, String comment) {
      String instrument = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";

      if (xml.startsWith(instrument)) {
         return instrument + comment + "\n" + xml.substring(instrument.length());
      } else {
         return comment + xml;
      }
   }
}
