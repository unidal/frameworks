package org.unidal.lookup.configuration;

import java.util.List;

import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public class Component {
   private ComponentModel m_model = new ComponentModel();

   public <T> Component(Class<T> roleClass) {
      this(roleClass, null, roleClass);
   }

   public <T> Component(Class<T> roleClass, Class<? extends T> implementationClass) {
      this(roleClass, null, implementationClass);
   }

   public <T> Component(Class<T> roleClass, Object roleHint, Class<? extends T> implementationClass) {
      m_model.setRole(roleClass.getName());
      m_model.setImplementation(implementationClass.getName());

      if (roleHint != null) {
         m_model.setRoleHint(roleHint.toString());
      }
   }

   public Component config(Configuration... children) {
      ConfigurationModel config = m_model.getConfiguration();

      if (config == null) {
         config = new ConfigurationModel();
         m_model.setConfiguration(config);
      }

      List<Any> configuration = config.getDynamicElements();

      for (Configuration child : children) {
         if (child != null) {
            configuration.add(new Any().setName(child.getName()).setValue(child.getValue()));
         }
      }

      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Component) {
         Component other = (Component) obj;

         String role1 = m_model.getRole();
         String role2 = other.m_model.getRole();

         if (!role1.equals(role2)) {
            return false;
         }

         String roleHint1 = m_model.getHint();
         String roleHint2 = other.m_model.getHint();

         if (roleHint1 == null && roleHint2 == null) {
            return true;
         } else if (roleHint1 != null && roleHint2 != null) {
            return roleHint1.equals(roleHint2);
         }
      }

      return false;
   }

   public ComponentModel getModel() {
      return m_model;
   }

   @Override
   public int hashCode() {
      String role = m_model.getRole();
      String roleHint = m_model.getHint();

      return role.hashCode() * 31 + (roleHint == null ? 0 : roleHint.hashCode());
   }

   public Component is(String instantiationStrategy) {
      m_model.setInstantiationStrategy(instantiationStrategy);
      return this;
   }

   public Component req(Class<?>... roleClasses) {
      for (Class<?> roleClass : roleClasses) {
         req(roleClass, "default", null);
      }

      return this;
   }

   public Component req(Class<?> roleClass, String roleHint) {
      return req(roleClass, roleHint, null);
   }

   public Component req(Class<?> roleClass, String roleHint, String fieldName) {
      RequirementModel requirement = new RequirementModel();

      requirement.setRole(roleClass.getName());
      requirement.setFieldName(fieldName);

      if (roleHint != null) {
         requirement.setRoleHint(roleHint);
      }

      m_model.addRequirement(requirement);
      return this;
   }

   /**
    * NOTES: non-exist required components identified by role hints will be ignored silently.
    * 
    * @param roleClass
    *           role class
    * @param roleHints
    *           role hints.
    * @param fieldName
    *           field name to inject
    * @return component definition
    */
   public Component req(Class<?> roleClass, String[] roleHints, String fieldName) {
      RequirementModel requirement = new RequirementModel();

      requirement.setRole(roleClass.getName());
      requirement.setFieldName(fieldName);

      Any hints = new Any().setName("role-hints");
      requirement.getDynamicElements().add(hints);

      for (String roleHint : roleHints) {
         hints.addChild(new Any().setName("role-hint").setValue(roleHint));
      }

      m_model.addRequirement(requirement);
      return this;
   }
   
   @Override
   public String toString() {
      return m_model.toString();
   }
}
