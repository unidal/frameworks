package org.unidal.lookup.configuration;

public class Configuration {
   private String m_name;

   private String m_value;

   public Configuration(String name) {
      m_name = name;
   }

   public String getName() {
      return m_name;
   }

   public String getValue() {
      return m_value;
   }

   public Configuration value(String value) {
      m_value = value;
      return this;
   }
}
