package org.unidal.dal.jdbc;

import java.util.BitSet;
import java.util.Map;

public abstract class DataObject {
   /* Indicates whether a field contains data or not */
   private BitSet m_usages;

   public DataObject() {
      m_usages = new BitSet();
   }

   /**
    * Called after the object is loaded with values from the database.
    */
   public void afterLoad() {
      // OVERRIDE IT IN SUB-CLASS
   }

   /**
    * Called before the object will be saved to the database.
    */
   public void beforeSave() {
      // OVERRIDE IT IN SUB-CLASS
   }

   protected void setFieldUsed(DataField field, boolean used) {
      if (used) {
         m_usages.set(field.getIndex());
      } else {
         m_usages.clear(field.getIndex());
      }
   }

   public boolean isFieldUsed(DataField field) {
      return m_usages.get(field.getIndex());
   }

   public Map<String, Object> getQueryHints() {
      // To be overridden
      return null;
   }

}
