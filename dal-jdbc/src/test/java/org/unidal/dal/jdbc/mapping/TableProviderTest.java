package org.unidal.dal.jdbc.mapping;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class TableProviderTest extends ComponentTestCase {
	@Test
	public void testLookup() throws Exception {
      TableProvider provider = lookup(TableProvider.class, "user");

      Assert.assertNotNull(provider);
   }
}
