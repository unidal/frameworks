package org.unidal.dal.jdbc.query.token.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class IfTokenResolverTest extends AbstractTokenResolverTest {
   @Test
   public void testEqFalse() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='EQ' field='key-user-id' value='1234'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1233);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testEqTrue() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='EQ' field='key-user-id' value='1234'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1234);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("...", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }
   
   @Test
   public void testEqTrueWithSlash() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
      "<IF type='EQ' field='key-user-id' value='1234'>a/b</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);
      
      user.setKeyUserId(1234);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("a/b", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testFalseAndParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_NULL' field='key-user-id'>${key-user-id}</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
      Assert.assertEquals(0, ctx.getParameters().size());
   }

   @Test
   public void testNotNullFalse() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_NULL' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testNotNullTrue() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_NULL' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1234);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("...", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testNotZeroFalse() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_ZERO' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(0);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testNotZeroTrue() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_ZERO' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1234);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("...", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testTrueAndParameter() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT,
            "<IF type='NOT_NULL' field='key-user-id'>${key-user-id}</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1234L);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("?", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
      Assert.assertEquals(1, ctx.getParameters().size());
      Assert.assertEquals(1234L, getParameterValue(ctx, 0));
   }

   @Test
   public void testZeroFalse() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IF type='ZERO' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(1234);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }

   @Test
   public void testZeroTrue() throws Exception {
      QueryDef query = new QueryDef("test", UserEntity.class, QueryType.SELECT, "<IF type='ZERO' field='key-user-id'>...</IF>");
      User user = new User();
      QueryContext ctx = getSelectContext(query, user, null);

      user.setKeyUserId(0);
      Assert.assertEquals(false, ctx.isWithinIfToken());
      m_queryResolver.resolve(ctx);
      Assert.assertEquals("...", ctx.getSqlStatement());
      Assert.assertEquals(false, ctx.isWithinIfToken());
   }
}
