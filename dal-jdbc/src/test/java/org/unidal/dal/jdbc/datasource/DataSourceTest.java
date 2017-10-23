package org.unidal.dal.jdbc.datasource;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.test.TestDataSourceManager;
import org.unidal.lookup.ComponentTestCase;

public class DataSourceTest extends ComponentTestCase {
   @Test
   public void testJdbcDataSource() throws Exception {
      define(TestDataSourceManager.class);

      try {
         DataSourceManager manager = lookup(DataSourceManager.class);
         DataSource ds = manager.getDataSource("jdbc-dal");

         // System.out.println(ds.getDescriptor());
         Assert.assertNotNull(ds.getConnection());
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database via JDBC, gave up");
         } else {
            throw e;
         }
      }
   }
}
