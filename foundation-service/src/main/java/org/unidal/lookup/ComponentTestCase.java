package org.unidal.lookup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.unidal.lookup.phase.PostConstructionPhase;

public abstract class ComponentTestCase {
   private MutablePlexusContainer m_container;

   private Map<Object, Object> m_context;

   private String m_basedir;

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

      LifecycleHandler plexus = configuration.getLifecycleHandlerManager().getLifecycleHandler("plexus");

      plexus.addBeginSegment(new PostConstructionPhase());

      return configuration;
   }

   protected MutablePlexusContainer getContainer() {
      return m_container;
   }

   protected String getCustomConfigurationName() {
      return null;
   }

   protected String getDefaultConfigurationName() throws Exception {
      return getClass().getName().replace('.', '/') + ".xml";
   }

   // ----------------------------------------------------------------------
   // Container access
   // ----------------------------------------------------------------------
   protected <T> T lookup(Class<T> role) throws Exception {
      return (T) getContainer().lookup(role);
   }

   protected <T> T lookup(Class<T> role, Object roleHint) throws Exception {
      return (T) getContainer().lookup(role, roleHint == null ? null : roleHint.toString());
   }

   protected <T> void release(T component) throws Exception {
      getContainer().release(component);
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

      m_container = new DefaultPlexusContainer(getConfiguration());
   }

   @After
   public void tearDown() throws Exception {
      if (m_container != null) {
         m_container.dispose();
         m_container = null;
      }
   }
}