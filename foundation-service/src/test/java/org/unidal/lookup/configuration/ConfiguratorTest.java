package org.unidal.lookup.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.unidal.helper.Files;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.configuration.ConfiguratorTest.AnnotatedCases.Annotated;
import org.unidal.lookup.configuration.ConfiguratorTest.LegacyCases.Legacy;
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

      ContainerLoader.destroyDefaultContainer();
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
   public void testAnnotatedCases() throws Exception {
      checkCodegen(new Annotated());
      checkLookup(new Annotated());
   }

   @Test
   public void testLegacyCases() throws Exception {
      checkCodegen(new Legacy());
      checkLookup(new Legacy());
   }

   interface AnnotatedCases {
      @Named(type = AT1.class)
      public static class AC11 implements AT1 {
      }

      @Named(type = AT1.class, value = "secondary")
      public static class AC12 implements AT1 {
      }

      @Named(type = AT3.class)
      public static class AC31 implements AT3, Initializable {
         @Inject
         private AT1 m_lt1;

         @Inject
         private AT2 m_lt2;

         public AT1 getLt1() {
            return m_lt1;
         }

         public AT2 getLt2() {
            return m_lt2;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertNotNull(m_lt1);
            Assert.assertNotNull(m_lt2);
         }
      }

      @Named(type = AT3.class, value = "secondary")
      public static class AC32 implements AT3, RoleHintEnabled, Initializable {
         @Inject
         private AT1 m_first;

         @Inject("secondary")
         private AT1 m_second;

         @InjectAttribute("test")
         private String m_type;

         @InjectAttribute("true")
         private boolean m_verbose;

         private String m_roleHint;

         @Override
         public void enableRoleHint(String roleHint) {
            m_roleHint = roleHint;
         }

         public AT1 getFirst() {
            return m_first;
         }

         public AT1 getSecond() {
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

         public void setFirst(AT1 first) {
            m_first = first;
         }

         public void setSecond(AT1 second) {
            m_second = second;
         }

         public void setType(String type) {
            m_type = type;
         }

         public void setVerbose(boolean verbose) {
            m_verbose = verbose;
         }
      }

      @Named(type = AT3.class, value = "third")
      public static class AC33 implements AT3, Initializable {
         @Inject({ "default", "secondary" })
         private List<AT1> m_list;

         public List<AT1> getList() {
            return m_list;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertEquals(2, m_list.size());
         }
      }

      @Named(type = AT4.class, value = Named.PER_LOOKUP, instantiationStrategy = Named.PER_LOOKUP)
      public static class AC41 implements AT4 {
      }

      @Named(type = AT4.class)
      public enum AC42 implements AT4 {
         E1, E2;
      }

      public static class Annotated extends AbstractResourceConfigurator {
         @Override
         public List<Component> defineComponents() {
            List<Component> all = new ArrayList<Component>();

            all.add(A(AC11.class));
            all.add(A(AC12.class));
            all.add(A(AT2.class));
            all.add(A(AC31.class));
            all.add(A(AC32.class));
            all.add(A(AC33.class));
            all.add(A(AC41.class));

            for (AC42 value : AC42.values()) {
               all.add(A(AC42.class, value.name()));
            }

            return all;
         }
      }

      public interface AT1 {
      }

      @Named
      public static class AT2 {
      }

      public interface AT3 {
      }

      public interface AT4 {
      }
   }

   interface LegacyCases {
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

      public enum LC42 implements LT4 {
         E1, E2;
      }

      public static class Legacy extends AbstractResourceConfigurator {
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
}
