package org.unidal.lookup.configuration;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

public class Configuration extends XmlPlexusConfiguration {
   public Configuration() {
      super("configuration");
   }

   public Configuration(String name, String... attributePairs) {
      super(name);

      if (attributePairs.length % 2 != 0) {
         throw new RuntimeException("Attribute name and value must be paired.");
      }

      for (int i = 0; i < attributePairs.length; i += 2) {
         setAttribute(attributePairs[i], attributePairs[i + 1]);
      }
   }

   public Configuration add(Configuration... children) {
      for (Configuration child : children) {
         if (child != null) {
            addChild(child);
         }
      }
      return this;
   }

   public Configuration value(String value) {
      setValue(value);
      return this;
   }
}
