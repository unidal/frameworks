package org.unidal.lookup.container.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.container.model.BaseEntity;
import org.unidal.lookup.container.model.IVisitor;

public class ConfigurationModel extends BaseEntity<ConfigurationModel> {
   private Boolean m_debug;

   private List<Any> m_dynamicElements = new ArrayList<Any>();

   public ConfigurationModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitConfiguration(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ConfigurationModel) {
         ConfigurationModel _o = (ConfigurationModel) obj;

         if (!equals(m_debug, _o.getDebug())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public List<Any> getDynamicElements() {
      return m_dynamicElements;
   }

   public Boolean getDebug() {
      return m_debug;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_debug == null ? 0 : m_debug.hashCode());

      return hash;
   }

   public boolean isDebug() {
      return m_debug != null && m_debug.booleanValue();
   }

   @Override
   public void mergeAttributes(ConfigurationModel other) {
   }

   public void setDynamicElements(List<Any> dynamicElements) {
      m_dynamicElements = dynamicElements;
   }

   public ConfigurationModel setDebug(Boolean debug) {
      m_debug = debug;
      return this;
   }

}
