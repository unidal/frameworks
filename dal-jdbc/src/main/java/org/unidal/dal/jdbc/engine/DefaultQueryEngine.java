package org.unidal.dal.jdbc.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.query.QueryExecutor;
import org.unidal.dal.jdbc.query.QueryResolver;
import org.unidal.dal.jdbc.transaction.TransactionManager;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = QueryEngine.class)
public class DefaultQueryEngine extends ContainerHolder implements QueryEngine {
   @Inject
   private EntityInfoManager m_entityManager;

   @Inject
   private QueryExecutor m_queryExecutor;

   @Inject
   private TransactionManager m_transactionManager;

   @Inject
   private QueryResolver m_queryResolver;

   protected <T extends DataObject> QueryContext createContext(QueryDef query, T proto) {
      QueryContext ctx = new DefaultQueryContext();
      EntityInfo enityInfo = m_entityManager.getEntityInfo(query.getEntityClass());
      Map<String, Object> queryHints = getQueryHints(query, proto);

      ctx.setQuery(query);
      ctx.setProto(proto);
      ctx.setEntityInfo(enityInfo);
      ctx.setQueryHints(queryHints);

      return ctx;
   }

   public <T extends DataObject> int[] deleteBatch(QueryDef query, T[] protos) throws DalException {
      if (protos.length == 0) {
         return new int[0];
      }

      QueryContext ctx = createContext(query, protos[0]);

      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdateBatch(ctx, protos);
   }

   public <T extends DataObject> int deleteSingle(QueryDef query, T proto) throws DalException {
      QueryContext ctx = createContext(query, proto);

      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdate(ctx);
   }

   protected Map<String, Object> getQueryHints(QueryDef query, DataObject proto) {
      Map<String, Object> hints = proto.getQueryHints();

      if (hints == null) {
         hints = new HashMap<String, Object>();
      }

      hints.put(HINT_QUERY, query);
      hints.put(HINT_DATA_OBJECT, proto);

      return hints;
   }

   public <T extends DataObject> int[] insertBatch(QueryDef query, T[] protos) throws DalException {
      if (protos.length == 0) {
         return new int[0];
      }

      QueryContext ctx = createContext(query, protos[0]);

      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdateBatch(ctx, protos);
   }

   public <T extends DataObject> int insertSingle(QueryDef query, T proto) throws DalException {
      QueryContext ctx = createContext(query, proto);

      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdate(ctx);
   }

   public <T extends DataObject> List<T> queryMultiple(QueryDef query, T proto, Readset<?> readset) throws DalException {
      QueryContext ctx = createContext(query, proto);

      ctx.setReadset(readset);
      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeQuery(ctx);
   }

   public <T extends DataObject> T querySingle(QueryDef query, T proto, Readset<?> readset) throws DalException {
      QueryContext ctx = createContext(query, proto);

      ctx.setReadset(readset);
      ctx.setFetchSize(1);
      m_queryResolver.resolve(ctx);

      List<T> results = m_queryExecutor.executeQuery(ctx);

      if (results.isEmpty()) {
         throw new DalNotFoundException("No record has been found for " + proto);
      } else {
         return results.get(0);
      }
   }

   public <T extends DataObject> int[] updateBatch(QueryDef query, T[] protos, Updateset<?> updateset)
         throws DalException {
      if (protos.length == 0) {
         return new int[0];
      }

      QueryContext ctx = createContext(query, protos[0]);

      ctx.setUpdateset(updateset);
      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdateBatch(ctx, protos);
   }

   public <T extends DataObject> int updateSingle(QueryDef query, T proto, Updateset<?> updateset) throws DalException {
      QueryContext ctx = createContext(query, proto);

      ctx.setUpdateset(updateset);
      m_queryResolver.resolve(ctx);
      return m_queryExecutor.executeUpdate(ctx);
   }
}
