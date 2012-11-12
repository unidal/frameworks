package org.unidal.lookup.configuration;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;

public class Component {
   private ComponentDescriptor<Object> m_descriptor;

   private List<ComponentRequirement> m_requirements;

   private Configuration m_configuration;

   public <T> Component(Class<T> roleClass) {
      this(roleClass, null, roleClass);
   }

   public <T> Component(Class<T> roleClass, Class<? extends T> implementationClass) {
      this(roleClass, null, implementationClass);
   }

   public <T> Component(Class<T> roleClass, Object roleHint, Class<? extends T> implementationClass) {
      m_descriptor = new ComponentDescriptor<Object>();
      m_descriptor.setRole(roleClass.getName());
      m_descriptor.setRoleHint(roleHint == null ? null : roleHint.toString());
      m_descriptor.setImplementation(implementationClass.getName());
      m_requirements = new ArrayList<ComponentRequirement>();
   }

   public Component config(Configuration... children) {
      if (m_configuration == null) {
         m_configuration = new Configuration();
      }

      for (Configuration child : children) {
         m_configuration.add(child);
      }

      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Component) {
         Component other = (Component) obj;

         String role1 = m_descriptor.getRole();
         String role2 = other.m_descriptor.getRole();

         if (!role1.equals(role2)) {
            return false;
         }

         String roleHint1 = m_descriptor.getRoleHint();
         String roleHint2 = other.m_descriptor.getRoleHint();

         if (roleHint1 == null && roleHint2 == null) {
            return true;
         } else if (roleHint1 != null && roleHint2 != null) {
            return roleHint1.equals(roleHint2);
         }
      }

      return false;
   }

   public Configuration getConfiguration() {
      return m_configuration;
   }

   public ComponentDescriptor<Object> getDescriptor() {
      return m_descriptor;
   }

   public List<ComponentRequirement> getRequirements() {
      return m_requirements;
   }

   @Override
   public int hashCode() {
      String role = m_descriptor.getRole();
      String roleHint = m_descriptor.getRoleHint();

      return role.hashCode() * 31 + (roleHint == null ? 0 : roleHint.hashCode());
   }

   public Component is(String instantiationStrategy) {
      m_descriptor.setInstantiationStrategy(instantiationStrategy);
      return this;
   }

   public Component lifecycle(String lifecycleHandler) {
      m_descriptor.setLifecycleHandler(lifecycleHandler);
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
      ComponentRequirement requirement = new ComponentRequirement();

      requirement.setRole(roleClass.getName());
      requirement.setRoleHint(roleHint);
      requirement.setFieldName(fieldName);

      m_requirements.add(requirement);
      return this;
   }

   public Component req(Class<?> roleClass, String[] roleHints, String fieldName) {
      ComponentRequirementList requirement = new ComponentRequirementList();
      List<String> hints = new ArrayList<String>();

      for (String hint : roleHints) {
         if (hint != null) {
            hints.add(hint);
         }
      }

      requirement.setRole(roleClass.getName());
      requirement.setRoleHints(hints);
      requirement.setFieldName(fieldName);

      m_requirements.add(requirement);
      return this;
   }
}
