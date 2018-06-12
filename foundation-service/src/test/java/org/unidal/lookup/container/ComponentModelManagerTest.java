package org.unidal.lookup.container;

import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ComponentModelManagerTest {
   @Test
   public void test() throws Exception {
      ComponentModelManager manager = new ComponentModelManager();
      List<URL> urls = manager.scanComponents();

      Assert.assertEquals(1, urls.size());
   }
}
