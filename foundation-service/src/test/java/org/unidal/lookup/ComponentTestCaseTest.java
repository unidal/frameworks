package org.unidal.lookup;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.formatter.Formatter;
import org.unidal.formatter.FormatterException;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.initialization.ModuleManager;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

public class ComponentTestCaseTest extends ComponentTestCase {
   @Test
   public void testComponentOverride() throws Exception {
      define(MockFormatter.class);

      Assert.assertEquals(MockFormatter.class, lookup(Formatter.class, Date.class.getName()).getClass());

      define(MockComponent.class) //
            .config("name", "mock") //
            .config("verbose", "true");

      MockComponent component = lookup(MockComponent.class);

      Assert.assertEquals(MockFormatter.class, component.getFormatter().getClass());
      Assert.assertEquals("mock", component.getName());
      Assert.assertEquals(true, component.isVerbose());
   }

   @Test
   public void testLookup() throws Exception {
      ModuleManager manager = lookup(ModuleManager.class);

      Assert.assertNotNull(manager);

      ModuleInitializer initializer = lookup(ModuleInitializer.class);

      Assert.assertNotNull(initializer);
   }

   @Test
   public void testUnmanagedComponent() throws Exception {
      getContainer().addComponent(this, getClass(), null);

      Assert.assertSame(this, lookup(getClass()));
   }

   @Named
   public static class MockComponent {
      @Inject("java.util.Date")
      private Formatter<Date> m_formatter;

      private boolean m_verbose;

      private String m_name;

      public Formatter<Date> getFormatter() {
         return m_formatter;
      }

      public String getName() {
         return m_name;
      }

      public boolean isVerbose() {
         return m_verbose;
      }

      public void setName(String name) {
         m_name = name;
      }

      public void setVerbose(boolean verbose) {
         m_verbose = verbose;
      }
   }

   @Named(type = Formatter.class, value = "java.util.Date")
   public static class MockFormatter implements Formatter<Date> {
      @Override
      public String format(String format, Date object) throws FormatterException {
         return null;
      }

      @Override
      public Date parse(String format, String text) throws FormatterException {
         return null;
      }
   }
}
