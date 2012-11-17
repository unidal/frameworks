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

public class JoinsTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testSelectOneTable() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<JOINS/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("1=1", ctx.getSqlStatement());
   }

   @Test
   public void testSelectOneTable2() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<JOINS/>");
      Readset<?> readset = UserEntity.READSET_FULL_U;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);
      
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("1=1", ctx.getSqlStatement());
   }
   
   @Test
   public void testSelectTwoTables() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<JOINS/>");
      Readset<?> readset = UserEntity.READSET_FULL_WITH_HOME_ADDRESS_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("u.user_id=hua.user_id and hua.type='H'", ctx.getSqlStatement());
   }

   @Test
   public void testInsert() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<JOINS/>");
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
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.UPDATE, "<JOINS/>");
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
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.DELETE, "<JOINS/>");
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
