package org.unidal.lookup.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.junit.Test;
import org.unidal.formatter.DateFormatter;
import org.unidal.formatter.Formatter;
import org.unidal.helper.Files;
import org.unidal.helper.Threads.LoggerThreadListener;
import org.unidal.helper.Threads.ThreadListener;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.configuration.ConfiguratorTest.LegacyCases.Legacy1;
import org.unidal.lookup.configuration.ConfiguratorTest.ModernCases.Simple1;
import org.unidal.lookup.extension.PostConstructionPhase;
import org.unidal.lookup.extension.RoleHintEnabled;

public class ConfiguratorTest {
   private void checkCodegen(AbstractResourceConfigurator configurator) throws IOException {
      List<Component> components = configurator.defineComponents();
      String path = getClass().getPackage().getName().replace('.', '/');
      String resource = "/" + path + "/" + configurator.getClass().getSimpleName() + ".xml";
      InputStream in = getClass().getResourceAsStream(resource);

      if (in == null) {
         throw new IllegalStateException(String.format("Resource(%s) is not found.", resource));
      }

      String expected = Files.forIO().readFrom(in, "utf-8");
      String actual = Configurators.forPlexus().generateXmlConfiguration(components);

      Assert.assertEquals(resource, expected, actual);
   }

   private void checkLookup(AbstractResourceConfigurator configurator) throws Exception {
      String path = getClass().getPackage().getName().replace('.', '/');
      String resource = path + "/" + configurator.getClass().getSimpleName() + ".xml";
      MutablePlexusContainer container = (MutablePlexusContainer) ContainerLoader
            .getDefaultContainer(getConfiguration(resource));
      List<Component> components = configurator.defineComponents();

      for (Component component : components) {
         String role = component.getDescriptor().getRole();
         String roleHint = component.getDescriptor().getRoleHint();

         // try lookup all components
         container.lookup(role, roleHint);
      }
   }

   private DefaultContainerConfiguration getConfiguration(String path) throws Exception {
      DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();
      Map<Object, Object> context = new HashMap<Object, Object>();

      context.put("basedir", new File(".").getAbsolutePath());
      context.put("plexus.home", new File("target/plexus-home").getAbsolutePath());
      configuration.setName("test").setContext(context);
      configuration.setContainerConfiguration(path);

      LifecycleHandler plexus = configuration.getLifecycleHandlerManager().getLifecycleHandler(
            PlexusConstants.PLEXUS_KEY);

      plexus.addBeginSegment(new PostConstructionPhase());

      return configuration;
   }

   @Test
   public void testLegacyCases() throws Exception {
      checkCodegen(new Legacy1());
      checkLookup(new Legacy1());
   }

   @Test
   public void testModernCases() throws IOException {
      checkCodegen(new Simple1());
   }

   public interface LegacyCases {
      public static class LC11 implements LT1 {
      }

      public static class LC12 implements LT1 {
      }

      public static class LC31 implements LT3, Initializable {
         private LT1 m_lt1;

         private LT2 m_lt2;

         public LT1 getLt1() {
            return m_lt1;
         }

         public LT2 getLt2() {
            return m_lt2;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertNotNull(m_lt1);
            Assert.assertNotNull(m_lt2);
         }
      }

      public static class LC32 implements LT3, RoleHintEnabled, Initializable {
         private LT1 m_first;

         private LT1 m_second;

         private String m_type;

         private boolean m_verbose;

         private String m_roleHint;

         @Override
         public void enableRoleHint(String roleHint) {
            m_roleHint = roleHint;
         }

         public LT1 getFirst() {
            return m_first;
         }

         public LT1 getSecond() {
            return m_second;
         }

         public String getType() {
            return m_type;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertNotNull(m_first);
            Assert.assertNotNull(m_second);
            Assert.assertEquals("test", m_type);
            Assert.assertEquals(true, m_verbose);
            Assert.assertEquals("secondary", m_roleHint);
         }

         public boolean isVerbose() {
            return m_verbose;
         }

         public void setFirst(LT1 first) {
            m_first = first;
         }

         public void setSecond(LT1 second) {
            m_second = second;
         }

         public void setType(String type) {
            m_type = type;
         }

         public void setVerbose(boolean verbose) {
            m_verbose = verbose;
         }
      }

      public static class LC33 implements LT3, Initializable {
         private List<LT1> m_list;

         public List<LT1> getList() {
            return m_list;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertEquals(2, m_list.size());
         }
      }

      public static class LC41 implements LT4 {
      }

      public static enum LC42 implements LT4 {
         E1, E2;
      }

      public static class Legacy1 extends AbstractResourceConfigurator {
         @Override
         public List<Component> defineComponents() {
            List<Component> all = new ArrayList<Component>();

            all.add(C(LT1.class, LC11.class));
            all.add(C(LT1.class, "secondary", LC12.class));
            all.add(C(LT2.class));
            all.add(C(LT3.class, LC31.class) //
                  .req(LT1.class, LT2.class));
            all.add(C(LT3.class, "secondary", LC32.class) //
                  .req(LT1.class, "default", "m_first") //
                  .req(LT1.class, "secondary", "m_second") //
                  .config(E("type").value("test"), E("verbose").value("true")));
            all.add(C(LT3.class, "third", LC33.class) //
                  .req(LT1.class, new String[] { "default", "secondary" }, "m_list"));
            all.add(C(LT4.class, PER_LOOKUP, LC41.class).is(PER_LOOKUP));

            for (LC42 value : LC42.values()) {
               all.add(C(LT4.class, value.name(), LC42.class).is(ENUM));
            }

            return all;
         }
      }

      public interface LT1 {
      }

      public static class LT2 {
      }

      public interface LT3 {
      }

      public interface LT4 {
      }
   }

   interface ModernCases {
      public static class C1 implements I1 {
      }

      public interface I1 {
      }

      public static class Simple1 extends AbstractResourceConfigurator {
         @Override
         public List<Component> defineComponents() {
            List<Component> all = new ArrayList<Component>();

            all.add(C(I1.class, C1.class));
            all.add(C(Formatter.class, Date.class.getName(), DateFormatter.class));
            all.add(C(ThreadListener.class, "logger", LoggerThreadListener.class));

            return all;
         }
      }
   }
}
