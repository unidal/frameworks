package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class ValueTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testUserName() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<value name='user-name'/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setUserName("test");
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?", ctx.getSqlStatement());
      Assert.assertEquals("[${user-name}]", ctx.getParameters().toString());
   }
   
   @Test
   public void testEncryptedPassword() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.INSERT, "<value name='encrypted-password'/>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setPassword("test");
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("password(?)", ctx.getSqlStatement());
      Assert.assertEquals("[${password}]", ctx.getParameters().toString());
   }
}
