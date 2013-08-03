package org.unidal.lookup;

import java.util.Date;

import junit.framework.Assert;

import org.codehaus.plexus.MutablePlexusContainer;
import org.junit.Test;
import org.unidal.formatter.Formatter;
import org.unidal.formatter.FormatterException;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.initialization.ModuleManager;

public class ComponentTestCaseTest extends ComponentTestCase {
   @Test
   public void testComponentOverride() throws Exception {
      defineComponent(Formatter.class, Date.class.getName(), MockFormatter.class);

      Assert.assertEquals(MockFormatter.class, lookup(Formatter.class, Date.class.getName()).getClass());
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
      MutablePlexusContainer container = getContainer();

      container.addComponent(this, getClass(), null);

      Assert.assertSame(this, lookup(getClass()));
   }

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
