package org.unidal.dal.jdbc.mapping;

import org.junit.Assert;
import org.unidal.lookup.ComponentTestCase;

public class TableProviderTest extends ComponentTestCase {
   public void testLookup() throws Exception {
      TableProvider provider = lookup(TableProvider.class, "user");

      Assert.assertNotNull(provider);
   }
}
