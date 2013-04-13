package org.unidal.dal.jdbc.query;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class QueryResolverTest extends ComponentTestCase {
	@Test
   public void testResolve() throws Exception {
      QueryResolver resolver = lookup(QueryResolver.class);
      Assert.assertNotNull(resolver);
   }
}
