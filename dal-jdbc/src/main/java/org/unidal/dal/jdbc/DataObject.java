package org.unidal.dal.jdbc;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public abstract class DataObject {
   /* Indicates whether a field contains data or not */
   private BitSet m_usages;

   private Map<String, Object> m_queryHints;

   public DataObject() {
      m_usages = new BitSet();
   }

   /**
    * Called after the object is loaded with values from the database.
    */
   public void afterLoad() {
      m_usages.clear();

      // OVERRIDE IT IN SUB-CLASS
   }

   /**
    * Called before the object will be saved to the database.
    */
   public void beforeSave() {
      // OVERRIDE IT IN SUB-CLASS
   }

   protected void clearUsage() {
      m_usages.clear();
   }

   public Map<String, Object> getQueryHints() {
      return m_queryHints;
   }

   public boolean isFieldUsed(DataField field) {
      return m_usages.get(field.getIndex());
   }

   protected void setFieldUsed(DataField field, boolean used) {
      if (used) {
         m_usages.set(field.getIndex());
      } else {
         m_usages.clear(field.getIndex());
      }
   }

   public void setQueryHint(String hint, Object value) {
      if (value == null) {
         if (m_queryHints != null) {
            m_queryHints.remove(hint);
         }
      } else {
         if (m_queryHints == null) {
            m_queryHints = new HashMap<String, Object>();
         }

         m_queryHints.put(hint, value);
      }
   }
}
