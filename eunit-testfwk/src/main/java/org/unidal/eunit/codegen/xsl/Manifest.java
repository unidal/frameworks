package org.unidal.eunit.codegen.xsl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Manifest {
   private String m_template;

   private String m_path;

   private FileMode m_mode;

   private Map<String, Object> m_properties;

   public Manifest(String template, String path, FileMode mode) {
      m_template = template;
      m_path = path;
      m_mode = mode;
   }

   public void addProperty(String name, Object value) {
      if (m_properties == null) {
         m_properties = new HashMap<String, Object>();
      }

      m_properties.put(name, value);
   }

   public FileMode getMode() {
      return m_mode;
   }

   public String getPath() {
      return m_path;
   }

   public Map<String, Object> getProperties() {
      if (m_properties == null) {
         return Collections.emptyMap();
      } else {
         return m_properties;
      }
   }

   public String getTemplate() {
      return m_template;
   }

   public void setTemplate(String template) {
      m_template = template;
   }

   @Override
   public String toString() {
      return String.format("Manifest[template=%s, path=%s, mode=%s, properties=%s]", m_template, m_path, m_mode, m_properties);
   }
}
