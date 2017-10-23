package org.unidal.dal.jdbc.datasource;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourceDef;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.entity.PropertiesDef;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.annotation.Named;

public class JdbcDataSourceDescriptorManagerTest extends ComponentTestCase {
   private void checkNames(JdbcDataSourceDescriptorManager manager, String... expected) {
      List<String> actual = manager.getDataSourceNames();

      Assert.assertEquals(Arrays.asList(expected).toString(), actual.toString());
   }

   private void checkProperty(JdbcDataSourceDescriptor descriptor, String property, String expected) {
      String actual = descriptor.getProperty(property, null);

      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testDynamic() throws Exception {
      define(OverwriteMockDataSourceProvider.class);
      define(MockDataSourceProvider.class);

      JdbcDataSourceDescriptorManager manager = lookup(JdbcDataSourceDescriptorManager.class);
      JdbcDataSourceDescriptor mock1 = manager.getDescriptor("mock1");
      JdbcDataSourceDescriptor mock2 = manager.getDescriptor("mock2");

      checkProperty(mock1, "user", "overwrite user user");
      checkProperty(mock1, "url", "overwrite user url");

      checkProperty(mock2, "user", "history user");
      checkProperty(mock2, "url", "history url");

      checkNames(manager, "mock1", "mock2", "jdbc-dal", "user", "history");
   }

   @Test
   public void testStatic() throws Exception {
      JdbcDataSourceDescriptorManager manager = lookup(JdbcDataSourceDescriptorManager.class);
      JdbcDataSourceDescriptor user = manager.getDescriptor("user");
      JdbcDataSourceDescriptor history = manager.getDescriptor("history");

      checkProperty(user, "user", "user");
      checkProperty(user, "url", "jdbc:mysql://localhost:3306/user?useUnicode=true&autoReconnect=true");

      checkProperty(history, "user", "history");
      checkProperty(history, "url", "jdbc:mysql://localhost:3306/history?useUnicode=true&autoReconnect=true");

      checkNames(manager, "jdbc-dal", "user", "history");
   }

   @Named(type = DataSourceProvider.class, value = "mock")
   public static class MockDataSourceProvider implements DataSourceProvider {
      @Override
      public DataSourcesDef defineDatasources() {
         DataSourcesDef def = new DataSourcesDef();

         def.addDataSource(new DataSourceDef("mock1").setProperties( //
               new PropertiesDef().setUrl("user url").setUser("user user")));

         def.addDataSource(new DataSourceDef("mock2").setProperties( //
               new PropertiesDef().setUrl("history url").setUser("history user")));

         return def;
      }
   }

   @Named(type = DataSourceProvider.class, value = "overwrite")
   public static class OverwriteMockDataSourceProvider implements DataSourceProvider {
      @Override
      public DataSourcesDef defineDatasources() {
         DataSourcesDef def = new DataSourcesDef();

         def.addDataSource(new DataSourceDef("mock1").setProperties( //
               new PropertiesDef().setUrl("overwrite user url").setUser("overwrite user user")));

         return def;
      }
   }
}
