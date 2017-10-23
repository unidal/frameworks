package org.unidal.dal.jdbc.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.query.QueryExecutor;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.annotation.Named;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserDao;
import org.unidal.test.user.dal.UserEntity;

public class TableProviderTest extends ComponentTestCase {
   @Test
   public void testLookup() throws Exception {
      TableProvider provider = lookup(TableProvider.class, "user");

      Assert.assertNotNull(provider);
   }

   @Test
   public void testUser() throws Exception {
      define(MockUserTableProvider.class);
      define(MockQueryExecutor.class);

      EntityInfoManager manager = lookup(EntityInfoManager.class);
      UserDao dao = lookup(UserDao.class);
      MockQueryExecutor executor = (MockQueryExecutor) lookup(QueryExecutor.class);

      manager.register(UserEntity.class);

      dao.findByPK(123, UserEntity.READSET_FULL);

      Assert.assertEquals(
            "SELECT u.user_id,u.full_name,u.creation_date,u.last_modified_date FROM user_3 u WHERE u.user_id = ?",
            executor.getSql());
   }

   @Named(type = QueryExecutor.class)
   public static class MockQueryExecutor implements QueryExecutor {
      private String m_sql;

      @SuppressWarnings("unchecked")
      @Override
      public <T extends DataObject> List<T> executeQuery(QueryContext ctx) throws DalException {
         ArrayList<T> list = new ArrayList<T>();

         m_sql = ctx.getSqlStatement();
         list.add((T) new User());
         return list;
      }

      @Override
      public int executeUpdate(QueryContext ctx) throws DalException {
         throw new UnsupportedOperationException("Not implemented!");
      }

      @Override
      public <T extends DataObject> int[] executeUpdateBatch(QueryContext ctx, T[] protos) throws DalException {
         throw new UnsupportedOperationException("Not implemented!");
      }

      public String getSql() {
         return m_sql;
      }
   }

   @Named(type = TableProvider.class, value = "user")
   public static class MockUserTableProvider implements TableProvider {
      private String m_dataSourceName = "user";

      @Override
      public String getDataSourceName(Map<String, Object> hints, String logicalTableName) {
         return m_dataSourceName;
      }

      @Override
      public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName) {
         User user = (User) hints.get(QueryEngine.HINT_DATA_OBJECT);

         return "user_" + (user.getKeyUserId() % 10);
      }

      public void setDataSourceName(String dataSourceName) {
         m_dataSourceName = dataSourceName;
      }
   }
}
