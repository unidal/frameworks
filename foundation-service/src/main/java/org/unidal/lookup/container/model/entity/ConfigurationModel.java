package org.unidal.lookup.container.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.container.model.BaseEntity;
import org.unidal.lookup.container.model.IVisitor;

public class ConfigurationModel extends BaseEntity<ConfigurationModel> {
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

         if (!getDynamicElements().equals(_o.getDynamicElements())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public List<Any> getDynamicElements() {
      return m_dynamicElements;
   }

   @Override
   public int hashCode() {
      int hash = 0;


      return hash;
   }

   @Override
   public void mergeAttributes(ConfigurationModel other) {
   }

   public void setDynamicElements(List<Any> dynamicElements) {
      m_dynamicElements = dynamicElements;
   }

}
