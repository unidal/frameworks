package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class FieldTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testField() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='user-id'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("u.user_id", ctx.getSqlStatement());
   }

   @Test
   public void testFieldOfSelectExpr() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='upper-user-name'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);
      
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("upper(full_name)", ctx.getSqlStatement());
   }

   @Test
   public void testFieldWithAnotherTable() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='user-id' table='home-address'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("hua.user_id", ctx.getSqlStatement());
   }
   
   @Test
   public void testFieldWithAnotherTableNonrelated() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='user-id' table='user2'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      try {
         m_queryResolver.resolve(ctx);

         Assert.fail("DalRuntimeException expected");
      } catch (DalRuntimeException e) {
         // expected
      }
   }

   @Test
   public void testFieldWithSameTable() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='user-id' table='user'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("u.user_id", ctx.getSqlStatement());
   }

   @Test
   public void testNonExistField() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<FIELD name='unknown'/>");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      try {
         m_queryResolver.resolve(ctx);

         Assert.fail("DalRuntimeException expected");
      } catch (DalRuntimeException e) {
         // expected
      }
   }
}
