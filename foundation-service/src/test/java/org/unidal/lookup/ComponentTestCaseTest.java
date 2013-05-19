package org.unidal.lookup;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.initialization.ModuleManager;

public class ComponentTestCaseTest extends ComponentTestCase {
   @Test
   public void testLookup() throws Exception {
      ModuleManager manager = lookup(ModuleManager.class);

      Assert.assertNotNull(manager);

      ModuleInitializer initializer = lookup(ModuleInitializer.class);

      Assert.assertNotNull(initializer);
   }
}
