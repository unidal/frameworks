package org.unidal.lookup;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.junit.After;
import org.junit.Before;
import org.unidal.lookup.configuration.Configuration;

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

   @SuppressWarnings("unchecked")
   protected <T> ComponentDefinition<T> defineComponent(Class<T> role, String roleHint,
         Class<? extends T> implementation) throws Exception {
      ComponentDescriptor<T> descriptor = new ComponentDescriptor<T>((Class<T>) implementation, null);

      descriptor.setRoleClass(role);
      descriptor.setRoleHint(roleHint);

      m_container.addComponentDescriptor(descriptor);
      return new ComponentDefinition<T>(descriptor);
   }

   @Override
   protected PlexusContainer getContainer() {
      return m_container;
   }

   @Before
   public void setUp() throws Exception {
      DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();
      String defaultConfigurationName = getClass().getName().replace('.', '/') + ".xml";

      configuration.setContainerConfiguration(defaultConfigurationName);
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
      private ComponentDescriptor<T> m_descriptor;

      private Configuration m_config;

      public ComponentDefinition(ComponentDescriptor<T> descriptor) {
         m_descriptor = descriptor;
      }

      public ComponentDefinition<T> config(String name, String value) {
         if (m_config == null) {
            m_config = new Configuration();
            m_descriptor.setConfiguration(m_config);
         }

         m_config.addChild(name, value);
         return this;
      }

      public ComponentDefinition<T> is(String instantiationStrategy) {
         m_descriptor.setInstantiationStrategy(instantiationStrategy);
         return this;
      }

      public ComponentDefinition<T> req(Class<?> role) {
         return req(role, null);
      }

      public ComponentDefinition<T> req(Class<?> role, String roleHint) {
         ComponentRequirement requirement = new ComponentRequirement();

         requirement.setRole(role.getName());
         requirement.setRoleHint(roleHint);

         m_descriptor.addRequirement(requirement);
         return this;
      }
   }
}