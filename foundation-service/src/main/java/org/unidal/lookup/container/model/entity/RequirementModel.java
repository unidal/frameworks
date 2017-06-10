package org.unidal.lookup.container.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.container.model.BaseEntity;
import org.unidal.lookup.container.model.IVisitor;

public class RequirementModel extends BaseEntity<RequirementModel> {
   private String m_role;

   private String m_roleHint = "default";

   private String m_fieldName;

   private List<Any> m_dynamicElements = new ArrayList<Any>();

   public RequirementModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitRequirement(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RequirementModel) {
         RequirementModel _o = (RequirementModel) obj;

         if (!equals(getRole(), _o.getRole())) {
            return false;
         }

         if (!equals(getRoleHint(), _o.getRoleHint())) {
            return false;
         }

         if (!equals(getFieldName(), _o.getFieldName())) {
            return false;
         }

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

   public String getFieldName() {
      return m_fieldName;
   }

   public String getRole() {
      return m_role;
   }

   public String getRoleHint() {
      return m_roleHint;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_role == null ? 0 : m_role.hashCode());
      hash = hash * 31 + (m_roleHint == null ? 0 : m_roleHint.hashCode());
      hash = hash * 31 + (m_fieldName == null ? 0 : m_fieldName.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(RequirementModel other) {
   }

   public void setDynamicElements(List<Any> dynamicElements) {
      m_dynamicElements = dynamicElements;
   }

   public RequirementModel setFieldName(String fieldName) {
      m_fieldName = fieldName;
      return this;
   }

   public RequirementModel setRole(String role) {
      m_role = role;
      return this;
   }

   public RequirementModel setRoleHint(String roleHint) {
      m_roleHint = roleHint;
      return this;
   }

}
