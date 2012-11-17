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

public class ParameterTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testNonExistField() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "${unknown}");
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
   public void testAttribute() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "${user-id}");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      user.setUserId(1234L);
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?", ctx.getSqlStatement());
      Assert.assertEquals(1234L, getParameterValue(ctx, 0));
   }
   
   @Test
   public void testAttributeWithDefaultValue() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "${user-id}");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?", ctx.getSqlStatement());
      Assert.assertEquals(0L, getParameterValue(ctx, 0));
   }

   @Test
   public void testVariable() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "${key-user-id}");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      user.setKeyUserId(1234L);
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?", ctx.getSqlStatement());
      Assert.assertEquals(1234L, getParameterValue(ctx, 0));
   }

}
