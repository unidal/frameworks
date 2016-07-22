package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class TablesTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testSelectOneTable() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<TABLES/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("`user` u", ctx.getSqlStatement());
      Assert.assertEquals(true, ctx.isTableResolved());
   }

   @Test
   public void testSelectTwoTables() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<TABLES/>");
      Readset<?> readset = UserEntity.READSET_FULL_WITH_HOME_ADDRESS_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("`user` u, user_address hua", ctx.getSqlStatement());
      Assert.assertEquals(true, ctx.isTableResolved());
   }

   @Test
   public void testSelectThreeTables() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<TABLES/>");
      Readset<?> readset = UserEntity.READSET_FULL_WITH_HOME_OFFICE_ADDRESS_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("`user` u, user_address hua, user_address oua", ctx.getSqlStatement());
      Assert.assertEquals(true, ctx.isTableResolved());
   }

   @Test
   public void testInsert() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<TABLES/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      try {
         m_queryResolver.resolve(ctx);

         Assert.fail("DalRuntimeException expected");
      } catch (DalRuntimeException e) {
         // expected
      }
   }

   @Test
   public void testUpdate() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.UPDATE, "<TABLES/>");
      Updateset<User> updateset = UserEntity.UPDATESET_FULL;
      User user = new User();
      QueryContext ctx = getUpdateContext(query, user, updateset);

      try {
         m_queryResolver.resolve(ctx);

         Assert.fail("DalRuntimeException expected");
      } catch (DalRuntimeException e) {
         // expected
      }
   }

   @Test
   public void testDelete() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.DELETE, "<TABLES/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      try {
         m_queryResolver.resolve(ctx);

         Assert.fail("DalRuntimeException expected");
      } catch (DalRuntimeException e) {
         // expected
      }
   }
}
