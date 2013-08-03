package org.unidal.dal.jdbc.datasource;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class JdbcDataSourceDescriptorManagerTest extends ComponentTestCase {
   @Test
   public void testMarshal() throws Exception {
      JdbcDataSourceDescriptorManager manager = lookup(JdbcDataSourceDescriptorManager.class);
      JdbcDataSourceDescriptor userConfig = manager.getDescriptor("user");
      JdbcDataSourceDescriptor historyConfig = manager.getDescriptor("history");

      Assert.assertEquals("user", userConfig.getProperty("user", null));
      Assert.assertEquals("jdbc:mysql://localhost:3306/user?useUnicode=true&autoReconnect=true",
            userConfig.getProperty("url", null));
      Assert.assertEquals("history", historyConfig.getProperty("user", null));
      Assert.assertEquals("jdbc:mysql://localhost:3306/history?useUnicode=true&autoReconnect=true",
            historyConfig.getProperty("url", null));
   }
}
