package org.unidal.dal.jdbc.query;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceException;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.entity.DataObjectAssembly;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.transaction.TransactionManager;
import org.unidal.helper.Stringizers;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.message.Message;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;

@Named(type = QueryExecutor.class)
public class DefaultQueryExecutor implements QueryExecutor {
   @Inject
   private TransactionManager m_transactionManager;

   @Inject
   private DataObjectAccessor m_accessor;

   @Inject
   private DataObjectAssembly m_assembly;

   @Inject
   private DataSourceManager m_dataSourceManager;

   @Inject
   private MessageProducer m_cat;

   protected PreparedStatement createPreparedStatement(QueryContext ctx) throws SQLException {
      Connection conn = m_transactionManager.getConnection(ctx);
      QueryDef query = ctx.getQuery();
      QueryType type = query.getType();
      PreparedStatement ps;

      if (type == QueryType.SELECT) {
         if (query.isStoreProcedure()) {
            ps = conn.prepareCall(ctx.getSqlStatement(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
         } else {
            ps = conn.prepareStatement(ctx.getSqlStatement(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
         }
      } else {
         ps = conn.prepareStatement(ctx.getSqlStatement(), PreparedStatement.RETURN_GENERATED_KEYS);
      }

      return ps;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends DataObject> List<T> executeQuery(QueryContext ctx) throws DalException {
      Transaction t = m_cat.newTransaction("SQL", getQueryName(ctx));
      T proto = (T) ctx.getProto();
      PreparedStatement ps = null;

      t.addData(ctx.getSqlStatement());

      try {
         ps = createPreparedStatement(ctx);

         // Set fetch size if have
         if (ctx.getFetchSize() > 0) {
            ps.setFetchSize(ctx.getFetchSize());
         }

         // Setup IN/OUT parameters
         setupInOutParameters(ctx, ps, proto, true);

         // log to CAT
         logCatEvent(ctx);

         // Execute the query
         ResultSet rs = ps.executeQuery();

         // Fetch all rows
         List<T> rows = m_assembly.assemble(ctx, rs);

         // Get OUT parameters if have
         retrieveOutParameters(ps, ctx.getParameters(), proto);

         t.setStatus(Transaction.SUCCESS);
         return rows;
      } catch (DataSourceException e) {
         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw e;
      } catch (Throwable e) {
         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw new DalException(String.format("Error when executing query(%s) failed, proto: %s, message: %s.",
               ctx.getSqlStatement(), proto, e), e);
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException e) {
               throw new DalRuntimeException("Error when closing PreparedStatement, message: " + e, e);
            }
         }

         t.complete();
         m_transactionManager.closeConnection();
      }
   }

   @Override
   public int executeUpdate(QueryContext ctx) throws DalException {
      Transaction t = m_cat.newTransaction("SQL", getQueryName(ctx));
      DataObject proto = ctx.getProto();
      PreparedStatement ps = null;

      t.addData(ctx.getSqlStatement());

      try {
         ps = createPreparedStatement(ctx);

         // Call beforeSave() to do some custom data manipulation
         proto.beforeSave();

         // Setup IN/OUT parameters
         setupInOutParameters(ctx, ps, proto, false);

         // log to CAT
         logCatEvent(ctx);

         // Execute the query
         int rowCount = ps.executeUpdate();

         // Get OUT parameters if have
         retrieveOutParameters(ps, ctx.getParameters(), proto);

         // Retrieve Generated Keys if have
         if (ctx.getQuery().getType() == QueryType.INSERT) {
            retrieveGeneratedKeys(ctx, ps, proto);
         }

         t.setStatus(Transaction.SUCCESS);
         return rowCount;
      } catch (DataSourceException e) {
         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw e;
      } catch (Throwable e) {
         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw new DalException(String.format("Error when executing update(%s) failed, proto: %s, message: %s.",
               ctx.getSqlStatement(), proto, e), e);
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException e) {
               throw new DalRuntimeException("Error when closing PreparedStatement, message: " + e, e);
            }
         }

         t.complete();
         m_transactionManager.closeConnection();
      }
   }

   @Override
   public <T extends DataObject> int[] executeUpdateBatch(QueryContext ctx, T[] protos) throws DalException {
      Transaction t = m_cat.newTransaction("SQL", getQueryName(ctx));
      PreparedStatement ps = null;
      int[] rowCounts = new int[protos.length];
      boolean inTransaction = m_transactionManager.isInTransaction();
      boolean updated = false;

      t.addData(ctx.getSqlStatement());

      try {
         ps = createPreparedStatement(ctx);

         if (ctx.getQuery().isStoreProcedure()) {
            if (!inTransaction) {
               ps.getConnection().setAutoCommit(false);
            }

            for (int i = 0; i < protos.length; i++) {
               // Call beforeSave() to do some custom data manipulation
               protos[i].beforeSave();

               // Setup IN/OUT parameters
               setupInOutParameters(ctx, ps, protos[i], false);

               if (i == 0) {
                  // log to CAT
                  logCatEvent(ctx);
               }

               // Execute the query
               rowCounts[i] = ps.executeUpdate();

               updated = true;

               // Get OUT parameters if have
               retrieveOutParameters(ps, ctx.getParameters(), protos[i]);

               // Retrieve Generated Keys if have
               if (ctx.getQuery().getType() == QueryType.INSERT) {
                  retrieveGeneratedKeys(ctx, ps, protos[i]);
               }
            }

            if (!inTransaction && updated) {
               ps.getConnection().commit();
               ps.getConnection().setAutoCommit(true);
            }
         } else {
            for (int i = 0; i < protos.length; i++) {
               // Call beforeSave() to do some custom data manipulation
               protos[i].beforeSave();

               // Setup IN/OUT parameters
               setupInOutParameters(ctx, ps, protos[i], false);

               if (i == 0) {
                  // log to CAT
                  logCatEvent(ctx);
               }

               ps.addBatch();
            }

            rowCounts = ps.executeBatch();

            // Unfortunately, getGeneratedKeys() is not supported by
            // executeBatch()
         }

         t.setStatus(Transaction.SUCCESS);
         return rowCounts;
      } catch (DataSourceException e) {
         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw e;
      } catch (Throwable e) {
         if (!inTransaction && updated) {
            try {
               ps.getConnection().rollback();
               ps.getConnection().setAutoCommit(true);
            } catch (SQLException sqle) {
               if (e instanceof SQLException) {
                  ((SQLException) e).setNextException(sqle);
               }
            }
         }

         t.setStatus(e.getClass().getSimpleName());
         m_cat.logError(e);
         m_transactionManager.reset();

         throw new DalException(String.format("Error when executing batch update(%s) failed, proto: %s, message: %s.",
               ctx.getSqlStatement(), ctx.getProto(), e), e);
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException e) {
               throw new DalRuntimeException("Error when closing PreparedStatement, message: " + e, e);
            }
         }

         t.complete();
         m_transactionManager.closeConnection();
      }
   }

   protected String getQueryName(QueryContext ctx) {
      QueryDef query = ctx.getQuery();
      EntityInfo entity = ctx.getEntityInfo();

      return entity.getLogicalName() + "." + query.getName();
   }

   protected void logCatEvent(QueryContext ctx) {
      DataSource ds = m_dataSourceManager.getDataSource(ctx.getDataSourceName());
      String url = ds.getDescriptor().getProperty("url", "no-url");
      String params = ctx.getParameterValues() == null ? null : Stringizers.forJson().from(ctx.getParameterValues());

      m_cat.logEvent("SQL.Method", ctx.getQuery().getType().name(), Message.SUCCESS, params);
      m_cat.logEvent("SQL.Database", url, Message.SUCCESS, null);
   }

   protected void retrieveGeneratedKeys(QueryContext ctx, PreparedStatement ps, DataObject proto) throws SQLException {
      EntityInfo entityInfo = ctx.getEntityInfo();
      ResultSet generatedKeys = null;
      boolean retrieved = false;

      for (DataField field : entityInfo.getAttributeFields()) {
         Attribute attribute = entityInfo.getAttribute(field);

         if (attribute != null && attribute.autoIncrement()) {
            if (!retrieved) {
               generatedKeys = ps.getGeneratedKeys();
               retrieved = true;
            }

            if (generatedKeys != null && generatedKeys.next()) {
               Object key = generatedKeys.getObject(1);
               m_accessor.setFieldValue(proto, field, key);

               break;
            }
         }
      }
   }

   protected <T extends DataObject> void retrieveOutParameters(PreparedStatement ps, List<Parameter> parameters, T proto)
         throws SQLException {
      if (ps instanceof CallableStatement) {
         int len = parameters.size();
         CallableStatement cs = (CallableStatement) ps;

         for (int i = 0; i < len; i++) {
            Parameter parameter = parameters.get(i);

            if (parameter.isOut()) {
               Object value = cs.getObject(i + 1);

               m_accessor.setFieldValue(proto, parameter.getField(), value);
            }
         }
      }
   }

   protected <T extends DataObject> void setupInOutParameters(QueryContext ctx, PreparedStatement ps, T proto,
         boolean prepareParameterValues) throws SQLException {
      List<Parameter> parameters = ctx.getParameters();
      int len = parameters.size();

      if (len > 0) {
         int index = 1;
         List<Object> m_parameterValues = prepareParameterValues ? new ArrayList<Object>() : null;

         for (int i = 0; i < len; i++, index++) {
            Parameter parameter = parameters.get(i);

            if (parameter.isIn()) {
               Object value = m_accessor.getFieldValue(proto, parameter.getField());

               if (parameter.isIterable()) { // Iterable
                  Iterable<?> iterable = (Iterable<?>) value;

                  for (Object item : iterable) {
                     ps.setObject(index++, item);

                     if (prepareParameterValues) {
                        m_parameterValues.add(item);
                     }
                  }

                  index--;
               } else if (parameter.isArray()) { // Array
                  int length = Array.getLength(value);

                  for (int j = 0; j < length; j++) {
                     Object item = Array.get(value, j);

                     ps.setObject(index++, item);

                     if (prepareParameterValues) {
                        m_parameterValues.add(item);
                     }
                  }

                  index--;
               } else {
                  ps.setObject(index, value);

                  if (prepareParameterValues) {
                     m_parameterValues.add(value);
                  }
               }
            }

            if (parameter.isOut() && ps instanceof CallableStatement) {
               int outType = parameter.getOutType();
               CallableStatement cs = (CallableStatement) ps;

               if (outType == Types.NUMERIC || outType == Types.DECIMAL) {
                  cs.registerOutParameter(index, outType, parameter.getOutScale());
               } else {
                  cs.registerOutParameter(index, outType);
               }
            }
         }

         if (prepareParameterValues && m_parameterValues.size() > 0) {
            ctx.setParameterValues(m_parameterValues.toArray(new Object[0]));
         }
      }
   }
}
