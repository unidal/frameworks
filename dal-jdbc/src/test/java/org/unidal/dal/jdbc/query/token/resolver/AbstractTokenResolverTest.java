package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.dal.jdbc.query.QueryResolver;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.user.address.dal.UserAddressEntity;
import org.unidal.test.user.dal.UserEntity;
import org.unidal.test.user.dal.invalid.User2Entity;

public abstract class AbstractTokenResolverTest extends ComponentTestCase {
   protected EntityInfoManager m_entityManager;

   protected QueryResolver m_queryResolver;

   private DataObjectAccessor m_dataObjectAccessor;

   @Override
   public void setUp() throws Exception {
      super.setUp();

      m_entityManager = lookup(EntityInfoManager.class);
      m_queryResolver = lookup(QueryResolver.class);
      m_dataObjectAccessor = lookup(DataObjectAccessor.class);

      m_entityManager.register(UserEntity.class);
      m_entityManager.register(User2Entity.class);
      m_entityManager.register(UserAddressEntity.class);
   }

   protected <T extends DataObject> QueryContext getSelectContext(QueryDef query, T proto, Readset<?> readset)
         throws Exception {
      QueryContext ctx = lookup(QueryContext.class);
      EntityInfo enityInfo = m_entityManager.getEntityInfo(query.getEntityClass());

      ctx.setQuery(query);
      ctx.setProto(proto);
      ctx.setReadset(readset);
      ctx.setEntityInfo(enityInfo);

      return ctx;
   }

   protected <T extends DataObject> QueryContext getUpdateContext(QueryDef query, T proto, Updateset<?> updateset)
         throws Exception {
      QueryContext ctx = lookup(QueryContext.class);
      EntityInfo enityInfo = m_entityManager.getEntityInfo(query.getEntityClass());

      ctx.setQuery(query);
      ctx.setProto(proto);
      ctx.setUpdateset(updateset);
      ctx.setEntityInfo(enityInfo);

      return ctx;
   }

   protected Object getParameterValue(QueryContext ctx, int index) {
      Parameter parameter = ctx.getParameters().get(index);

      return m_dataObjectAccessor.getFieldValue(ctx.getProto(), parameter.getField());
   }
}
