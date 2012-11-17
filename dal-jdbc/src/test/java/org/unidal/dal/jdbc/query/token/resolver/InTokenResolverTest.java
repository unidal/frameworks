package org.unidal.dal.jdbc.query.token.resolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class InTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testInConstant() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>...</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(...)", ctx.getSqlStatement());
      Assert.assertEquals(0, ctx.getParameters().size());
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInNormalParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setUserId(1234L);
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(?)", ctx.getSqlStatement());
      Assert.assertEquals(1, ctx.getParameters().size());
      Assert.assertEquals(1234L, getParameterValue(ctx, 0));
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInArrayParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id-array}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);
      long[] userIdArray = new long[] { 1L, 2L, 3L, 4L };

      user.setUserIdArray(userIdArray);
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(?,?,?,?)", ctx.getSqlStatement());
      Assert.assertEquals(1, ctx.getParameters().size());
      Assert.assertEquals(userIdArray, getParameterValue(ctx, 0));
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInEmptyArrayParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id-array}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setUserIdArray(new long[0]);
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(null)", ctx.getSqlStatement());
      Assert.assertEquals(0, ctx.getParameters().size());
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInListParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id-list}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setUserIdList(Arrays.asList(1L, 2L, 3L, 4L));
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(?,?,?,?)", ctx.getSqlStatement());
      Assert.assertEquals(1, ctx.getParameters().size());
      Assert.assertEquals("[1, 2, 3, 4]", getParameterValue(ctx, 0).toString());
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInEmptyListParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id-list}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);
      List<Long> emptyList = Collections.emptyList();

      user.setUserIdList(emptyList);
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(null)", ctx.getSqlStatement());
      Assert.assertEquals(0, ctx.getParameters().size());
      Assert.assertEquals(false, ctx.isWithinInToken());
   }

   @Test
   public void testInBothParameters() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IN>${user-id},${user-id-array}</IN>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);
      long[] userIdArray = new long[] { 1, 2, 3, 4 };

      user.setUserId(1234L);
      user.setUserIdArray(userIdArray);
      Assert.assertEquals(false, ctx.isWithinInToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("(?,?,?,?,?)", ctx.getSqlStatement());
      Assert.assertEquals(2, ctx.getParameters().size());
      Assert.assertEquals(1234L, getParameterValue(ctx, 0));
      Assert.assertEquals(userIdArray, getParameterValue(ctx, 1));
      Assert.assertEquals(false, ctx.isWithinInToken());
   }
}
