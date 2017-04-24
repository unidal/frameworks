package org.unidal.lookup;

import org.codehaus.plexus.PlexusContainer;
import org.junit.After;
import org.junit.Before;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public abstract class ComponentTestCase extends ContainerHolder {
   protected static final String PER_LOOKUP = "per-lookup";

   private PlexusContainer m_container;

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role) throws Exception {
      return defineComponent(role, null, role);
   }

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role, Class<? extends T> implementation)
         throws Exception {
      return defineComponent(role, null, implementation);
   }

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role, String roleHint,
         Class<? extends T> implementation) throws Exception {
      ComponentModel model = new ComponentModel();

      model.setImplementation(implementation.getName());
      model.setRole(role.getName());

      if (roleHint != null) {
         model.setRoleHint(roleHint);
      }

      m_container.addComponentModel(model);
      return new ComponentDefinition<T>(model);
   }

   @Override
   protected PlexusContainer getContainer() {
      return m_container;
   }

   @Before
   public void setUp() throws Exception {
      String configuration = getClass().getName().replace('.', '/') + ".xml";

      ContainerLoader.destroy();
      m_container = ContainerLoader.getDefaultContainer(configuration);
      System.setProperty("devMode", "true");
   }

   @After
   public void tearDown() throws Exception {
      ContainerLoader.destroy();
      m_container = null;
   }

   protected static final class ComponentDefinition<T> {
      private ComponentModel m_model;

      public ComponentDefinition(ComponentModel descriptor) {
         m_model = descriptor;
      }

      public ComponentDefinition<T> config(String name, String value) {
         Any element = new Any().setName(name).setValue(value);
         ConfigurationModel config = m_model.getConfiguration();

         if (config == null) {
            config = new ConfigurationModel();
            m_model.setConfiguration(config);
         }

         config.getDynamicElements().add(element);
         return this;
      }

      public ComponentDefinition<T> is(String instantiationStrategy) {
         m_model.setInstantiationStrategy(instantiationStrategy);
         return this;
      }

      public ComponentDefinition<T> req(Class<?> role) {
         return req(role, null);
      }

      public ComponentDefinition<T> req(Class<?> role, String roleHint) {
         RequirementModel requirement = new RequirementModel();

         requirement.setRole(role.getName());

         if (roleHint != null) {
            requirement.setRoleHint(roleHint);
         }

         m_model.addRequirement(requirement);
         return this;
      }
   }
}