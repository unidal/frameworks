package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class StringTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testString() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "anything");
      Readset<?> readset = UserEntity.READSET_FULL;
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, readset);

      m_queryResolver.resolve(ctx);
      Assert.assertEquals("anything", ctx.getSqlStatement());
   }
}
