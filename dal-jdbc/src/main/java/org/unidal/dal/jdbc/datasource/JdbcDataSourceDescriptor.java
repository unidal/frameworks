package org.unidal.dal.jdbc.datasource;

import java.util.LinkedHashMap;
import java.util.Map;

public class JdbcDataSourceDescriptor implements DataSourceDescriptor {
   private String m_id;

   private String m_type;

   private Map<String, Object> m_properties = new LinkedHashMap<String, Object>();

   @Override
   public boolean getBooleanProperty(String name, boolean defaultValue) {
      Object value = m_properties.get(name);

      if (value != null) {
         if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
         } else {
            return "true".equals(value);
         }
      }

      return defaultValue;
   }

   @Override
   public double getDoubleProperty(String name, double defaultValue) {
      Object value = m_properties.get(name);

      if (value != null) {
         if (value instanceof Double) {
            return ((Double) value).doubleValue();
         } else {
            try {
               return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
               // ignore
            }
         }
      }

      return defaultValue;
   }

   @Override
   public String getId() {
      return m_id;
   }

   @Override
   public int getIntProperty(String name, int defaultValue) {
      Object value = m_properties.get(name);

      if (value != null) {
         if (value instanceof Integer) {
            return ((Integer) value).intValue();
         } else {
            try {
               return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
               // ignore
            }
         }
      }

      return defaultValue;
   }

   @Override
   public long getLongProperty(String name, long defaultValue) {
      Object value = m_properties.get(name);

      if (value != null) {
         if (value instanceof Long) {
            return ((Long) value).longValue();
         } else {
            try {
               return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
               // ignore
            }
         }
      }

      return defaultValue;
   }

   @Override
   public Map<String, Object> getProperties() {
      return m_properties;
   }

   @Override
   public String getProperty(String name, String defaultValue) {
      Object value = m_properties.get(name);

      if (value != null) {
         return value.toString();
      } else {
         return defaultValue;
      }
   }

   @Override
   public String getType() {
      return m_type;
   }

   public void setId(String id) {
      m_id = id;
   }

   public void setProperty(String name, Object value) {
      m_properties.put(name, value);
   }

   public void setType(String type) {
      m_type = type;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(256);

      sb.append("JdbcDataSourceDescriptor[");
      sb.append("id:").append(m_id);
      sb.append(",type:").append(m_type);
      sb.append(",properties:").append(m_properties);
      sb.append("]");

      return sb.toString();
   }
}
