package org.unidal.dal.jdbc.query;

import org.junit.Assert;
import org.unidal.lookup.ComponentTestCase;

public class QueryResolverTest extends ComponentTestCase {
   public void testResolve() throws Exception {
      QueryResolver resolver = lookup(QueryResolver.class, "MySql");
      Assert.assertNotNull(resolver);
   }
}
