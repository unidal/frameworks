package org.unidal.lookup.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.configuration.ConfiguratorTest.AnnotatedCases.Annotated;
import org.unidal.lookup.configuration.ConfiguratorTest.LegacyCases.Legacy;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
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
      String actual = Configurators.forPlexus().generateXmlConfiguration(configurator, components);

      Assert.assertEquals(resource, expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"));
   }

   private void checkLookup(AbstractResourceConfigurator configurator) throws Exception {
      String path = getClass().getPackage().getName().replace('.', '/');
      String resource = path + "/" + configurator.getClass().getSimpleName() + ".xml";
      PlexusContainer container = ContainerLoader.getDefaultContainer(resource);
      List<Component> components = configurator.defineComponents();

      for (Component component : components) {
         String role = component.getModel().getRole();
         String roleHint = component.getModel().getHint();

         // try lookup all components
         container.lookup(Class.forName(role), roleHint);
      }

      ContainerLoader.destroy();
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

      @Named(type = AT1.class, value = "third", instantiationStrategy = Named.PER_LOOKUP)
      public static class AC13 implements AT1 {
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
         @Inject
         private List<AT1> m_list;

         @Override
         public void initialize() throws InitializationException {
            Assert.assertEquals(3, m_list.size());
         }
      }

      @Named(type = AT3.class, value = "forth")
      public static class AC34 implements AT3, Initializable {
         @Inject
         private List<AT1> m_list;

         @Inject
         private Set<AT1> m_set;

         @Inject
         private Collection<AT1> m_collection;

         @Inject
         private Map<String, AT1> m_map;

         @Inject
         private AT1[] m_array;

         @Inject({ "default", "secondary" })
         private List<AT1> m_list2;

         @Inject({ "default", "secondary" })
         private Set<AT1> m_set2;

         @Inject({ "default", "secondary" })
         private Collection<AT1> m_collection2;

         @Inject({ "default", "secondary" })
         private Map<String, AT1> m_map2;

         @Inject({ "default", "secondary" })
         private AT1[] m_array2;

         @Override
         public void initialize() throws InitializationException {
            Assert.assertEquals(3, m_list.size());
            Assert.assertEquals(3, m_set.size());
            Assert.assertEquals(3, m_collection.size());
            Assert.assertEquals(3, m_map.size());
            Assert.assertEquals(3, m_array.length);

            Assert.assertEquals(2, m_list2.size());
            Assert.assertEquals(2, m_set2.size());
            Assert.assertEquals(2, m_collection2.size());
            Assert.assertEquals(2, m_map2.size());
            Assert.assertEquals(2, m_array2.length);
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
            all.add(A(AC13.class));
            all.add(A(AT2.class));
            all.add(A(AC31.class));
            all.add(A(AC32.class));
            all.add(A(AC33.class));
            all.add(A(AC34.class));
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
      @Named(type = LT1.class)
      public static class LC11 implements LT1 {
      }

      @Named(type = LT1.class, value = "secondary")
      public static class LC12 implements LT1 {
      }

      @Named(type = LT3.class)
      public static class LC31 implements LT3, Initializable {
         @Inject
         private LT1 m_lt1;

         @Inject
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

      @Named(type = LT3.class, value = "secondary")
      public static class LC32 implements LT3, RoleHintEnabled, Initializable {
         @Inject
         private LT1 m_first;

         @Inject("secondary")
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

      @Named(type = LT3.class, value = "third")
      public static class LC33 implements LT3, Initializable {
         @Inject({ "default", "secondary" })
         private List<LT1> m_list;

         public List<LT1> getList() {
            return m_list;
         }

         @Override
         public void initialize() throws InitializationException {
            Assert.assertEquals(2, m_list.size());
         }
      }

      @Named(type = LT4.class, value = Named.PER_LOOKUP, instantiationStrategy = Named.PER_LOOKUP)
      public static class LC41 implements LT4 {
      }

      @Named(type = LT4.class)
      public enum LC42 implements LT4 {
         E1, E2;
      }

      public static class Legacy extends AbstractResourceConfigurator {
         @Override
         public List<Component> defineComponents() {
            List<Component> all = new ArrayList<Component>();

            all.add(A(LC11.class));
            all.add(A(LC12.class));
            all.add(A(LT2.class));
            all.add(A(LC31.class));
            all.add(A(LC32.class).config(E("type").value("test"), E("verbose").value("true")));
            all.add(A(LC33.class));
            all.add(A(LC41.class));

            for (LC42 value : LC42.values()) {
               all.add(A(LC42.class, value.name()));
            }

            return all;
         }
      }

      public interface LT1 {
      }

      @Named
      public static class LT2 {
      }

      public interface LT3 {
      }

      public interface LT4 {
      }
   }
}
