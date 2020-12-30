package org.unidal.dal.jdbc.query.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.unidal.cat.Cat;
import org.unidal.cat.message.Message;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.helper.Files;
import org.unidal.helper.Stringizers;
import org.unidal.lookup.annotation.Inject;

public abstract class MysqlBaseHandler {
   @Inject
   private DataObjectAccessor m_accessor;

   @Inject
   private DataSourceManager m_dataSourceManager;

   protected PreparedStatement createPreparedStatement(QueryContext ctx, Connection conn) throws SQLException {
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

   protected String getQueryName(QueryContext ctx) {
      QueryDef query = ctx.getQuery();
      EntityInfo entity = ctx.getEntityInfo();

      return entity.getLogicalName() + "." + query.getName();
   }

   protected void logCatEvent(QueryContext ctx) {
      DataSource ds = m_dataSourceManager.getDataSource(ctx.getDataSourceName());
      String url = ds.getDescriptor().getProperty("url", "no-url");
      String params = ctx.getParameterValues() == null ? null : Stringizers.forJson().from(ctx.getParameterValues());
      int pos = url.indexOf('?');

      Cat.logEvent("SQL.Method", ctx.getQuery().getType().name(), Message.SUCCESS, params);

      if (pos > 0) {
         Cat.logEvent("SQL.Database", url.substring(0, pos), url);
      } else {
         Cat.logEvent("SQL.Database", url);
      }
   }

   protected void retrieveGeneratedKeys(QueryContext ctx, ResultSet generatedKeys, DataObject proto) throws SQLException {
      EntityInfo entityInfo = ctx.getEntityInfo();
      DataField field = entityInfo.getAutoIncrementField();

      if (field != null && generatedKeys != null && generatedKeys.next()) {
         Object key = generatedKeys.getObject(1);

         m_accessor.setFieldValue(proto, field, key);
      }
   }

   protected void retrieveGeneratedKeys(QueryContext ctx, ResultSet generatedKeys, DataObject[] protos) throws SQLException {
      EntityInfo entityInfo = ctx.getEntityInfo();
      DataField field = entityInfo.getAutoIncrementField();

      for (DataObject proto : protos) {
         if (field != null && generatedKeys != null && generatedKeys.next()) {
            Object key = generatedKeys.getObject(1);

            m_accessor.setFieldValue(proto, field, key);
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
                  if (value instanceof InputStream) {
                     InputStream in = (InputStream) value;

                     try {
                        int length = in.available();

                        ps.setBinaryStream(index, in, length);
                     } catch (IOException e) {
                        try {
                           byte[] ba = Files.forIO().readFrom(in);

                           ps.setObject(index, ba);
                        } catch (IOException ex) {
                           throw new RuntimeException("Erro when reading " + parameter.getField().getName() + "!", ex);
                        }
                     }
                  } else {
                     ps.setObject(index, value);
                  }

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
