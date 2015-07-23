package org.unidal.dal.jdbc.query;

import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = QueryExecutor.class)
public class DefaultQueryExecutor implements QueryExecutor {
   @Inject("mysql")
   private ReadHandler m_readHandler;

   @Inject("mysql")
   private WriteHandler m_writeHandler;

   @Override
   public <T extends DataObject> List<T> executeQuery(QueryContext ctx) throws DalException {
      return m_readHandler.executeQuery(ctx);
   }

   @Override
   public int executeUpdate(QueryContext ctx) throws DalException {
      return m_writeHandler.executeUpdate(ctx);
   }

   @Override
   public <T extends DataObject> int[] executeUpdateBatch(QueryContext ctx, T[] protos) throws DalException {
      return m_writeHandler.executeUpdateBatch(ctx, protos);
   }
}
