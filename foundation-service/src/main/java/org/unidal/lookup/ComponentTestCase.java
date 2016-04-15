package org.unidal.lookup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.unidal.helper.Reflects;
import org.unidal.lookup.configuration.Configuration;
import org.unidal.lookup.extension.EnumComponentManagerFactory;
import org.unidal.lookup.extension.PostConstructionPhase;

import com.google.common.collect.Multimap;

public abstract class ComponentTestCase extends ContainerHolder {
   protected static final String PER_LOOKUP = "per-lookup";

   private MutablePlexusContainer m_container;

   private Map<Object, Object> m_context;

   private String m_basedir;

   protected void browse(String url) throws IOException {
      java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
   }

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
      if (roleHint == null) {
         roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
      }

      ComponentDescriptor<T> descriptor = new ComponentDescriptor<T>((Class<T>) implementation,
            m_container.getContainerRealm());

      descriptor.setRoleClass(role);
      descriptor.setRoleHint(roleHint);

      Map<ClassRealm, SortedMap<String, Multimap<String, ComponentDescriptor<?>>>> index = Reflects.forField()
            .getDeclaredFieldValue(m_container, "componentRegistry", "repository", "index");
      for (SortedMap<String, Multimap<String, ComponentDescriptor<?>>> roleIndex : index.values()) {
         Multimap<String, ComponentDescriptor<?>> roleHintIndex = roleIndex.get(role.getName());

         if (roleHintIndex != null) {
            roleHintIndex.removeAll(roleHint);
         }
      }

      m_container.addComponentDescriptor(descriptor);

      return new ComponentDefinition<T>(descriptor);
   }

   protected String getBaseDir() {
      if (m_basedir != null) {
         return m_basedir;
      }

      m_basedir = System.getProperty("basedir");

      if (m_basedir == null) {
         try {
            m_basedir = new File(".").getCanonicalPath();
         } catch (IOException e) {
            m_basedir = "";
         }
      }

      return m_basedir;
   }

   protected DefaultContainerConfiguration getConfiguration() throws Exception, UndefinedLifecycleHandlerException {
      String customConfigurationName = getCustomConfigurationName();
      DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();

      configuration.setName("test").setContext(m_context);

      if (customConfigurationName != null) {
         configuration.setContainerConfiguration(customConfigurationName);
      } else {
         String defaultConfigurationName = getDefaultConfigurationName();

         configuration.setContainerConfiguration(defaultConfigurationName);
      }

      LifecycleHandler plexus = configuration.getLifecycleHandlerManager().getLifecycleHandler(
            PlexusConstants.PLEXUS_KEY);

      plexus.addBeginSegment(new PostConstructionPhase());

      return configuration;
   }

   @Override
   public MutablePlexusContainer getContainer() {
      return (MutablePlexusContainer) super.getContainer();
   }

   protected String getCustomConfigurationName() {
      return null;
   }

   protected String getDefaultConfigurationName() throws Exception {
      return getClass().getName().replace('.', '/') + ".xml";
   }

   @Before
   public void setUp() throws Exception {
      m_basedir = getBaseDir();

      // ----------------------------------------------------------------------------
      // Context Setup
      // ----------------------------------------------------------------------------

      m_context = new HashMap<Object, Object>();

      m_context.put("basedir", m_basedir);

      boolean hasPlexusHome = m_context.containsKey("plexus.home");

      if (!hasPlexusHome) {
         File f = new File(m_basedir, "target/plexus-home");

         if (!f.isDirectory()) {
            f.mkdir();
         }

         m_context.put("plexus.home", f.getAbsolutePath());
      }

      m_container = (MutablePlexusContainer) ContainerLoader.getDefaultContainer(getConfiguration());
      m_container.getComponentRegistry().registerComponentManagerFactory(new EnumComponentManagerFactory());

      System.setProperty("devMode", "true");

      super.setContainer(m_container);
   }

   @After
   public void tearDown() throws Exception {
      ContainerLoader.destroyDefaultContainer();
   }

   public static final class ComponentDefinition<T> {
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

         if (roleHint != null) {
            requirement.setRoleHint(roleHint);
         } else {
            requirement.setRoleHint("default");
         }

         m_descriptor.addRequirement(requirement);
         return this;
      }
   }
}