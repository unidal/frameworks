package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class ValuesTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testInsert1() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<values/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setUserName("test");
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?,NOW(),NOW(),password(?)", ctx.getSqlStatement());
      Assert.assertEquals("[${user-name}, ${password}]", ctx.getParameters().toString());
   }
   
   @Test
   public void testInsert2() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<values/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);
      
      user.setUserId(1234);
      user.setUserName("test");
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?,?,NOW(),NOW(),password(?)", ctx.getSqlStatement());
      Assert.assertEquals("[${user-id}, ${user-name}, ${password}]", ctx.getParameters().toString());
   }
}
