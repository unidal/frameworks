package org.unidal.dal.jdbc.datasource;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class JdbcDataSourceConfigurationManagerTest extends ComponentTestCase {
	@Test
	public void testMarshal() throws Exception {
      JdbcDataSourceConfigurationManager manager = lookup(JdbcDataSourceConfigurationManager.class);
      JdbcDataSourceConfiguration userConfig = manager.getConfiguration("user");
      JdbcDataSourceConfiguration historyConfig = manager.getConfiguration("history");

      Assert.assertEquals("user", userConfig.getUser());
      Assert.assertEquals("jdbc:mysql://localhost:3306/user?useUnicode=true&autoReconnect=true", userConfig.getUrl());
      Assert.assertEquals("history", historyConfig.getUser());
      Assert.assertEquals("jdbc:mysql://localhost:3306/history?useUnicode=true&autoReconnect=true", historyConfig.getUrl());
   }
}
