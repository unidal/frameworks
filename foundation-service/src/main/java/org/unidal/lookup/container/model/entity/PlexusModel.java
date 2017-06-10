package org.unidal.lookup.container.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.container.model.BaseEntity;
import org.unidal.lookup.container.model.IVisitor;

public class PlexusModel extends BaseEntity<PlexusModel> {
   private List<ComponentModel> m_components = new ArrayList<ComponentModel>();

   public PlexusModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitPlexus(this);
   }

   public PlexusModel addComponent(ComponentModel component) {
      m_components.add(component);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PlexusModel) {
         PlexusModel _o = (PlexusModel) obj;

         if (!equals(getComponents(), _o.getComponents())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public List<ComponentModel> getComponents() {
      return m_components;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      for (ComponentModel e : m_components) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(PlexusModel other) {
   }

}
