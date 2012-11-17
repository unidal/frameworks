package org.unidal.dal.jdbc.raw;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;

public class RawDataObject extends DataObject {
   private Map<String, Object> m_map = new LinkedHashMap<String, Object>();

   public Set<String> getFieldNames() {
      return m_map.keySet();
   }

   public Set<Entry<String, Object>> getFields() {
      return m_map.entrySet();
   }

   public Object getFieldValue(String fieldName) {
      return m_map.get(fieldName);
   }

   @Override
   public void setFieldUsed(DataField field, boolean used) {
      super.setFieldUsed(field, used);
   }

   public void setFieldValue(String fieldName, Object value) {
      m_map.put(fieldName, value);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(1024);
      boolean first = true;

      sb.append("RawDataObject[");

      for (Map.Entry<String, Object> e : m_map.entrySet()) {
         if (first) {
            first = false;
         } else {
            sb.append(", ");
         }

         sb.append(e.getKey()).append(": ").append(e.getValue());
      }

      sb.append("]");
      return sb.toString();
   }
}
