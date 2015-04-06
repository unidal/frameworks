package org.unidal.dal.jdbc.query.token.resolver;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.annotation.Variable;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.dal.jdbc.query.token.ParameterToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * ${<i>parameter-name</i>} or #{<i>parameter-name</i>}
 */
@Named(type = TokenResolver.class, value = "PARAM")
public class ParameterTokenResolver implements TokenResolver {
   @Inject
   private DataObjectAccessor m_accessor;

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.PARAM) {
         throw new DalRuntimeException("Internal error: only PARAM token is supported by " + getClass());
      }

      ParameterToken parameter = (ParameterToken) token;
      String fieldName = parameter.getParameterName();
      DataField dataField = ctx.getEntityInfo().getFieldByName(fieldName);

      if (parameter.isIn()) { // IN
         Object value = m_accessor.getFieldValue(ctx.getProto(), dataField);

         if (ctx.isWithinInToken() && value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);
            StringBuilder sb = new StringBuilder();

            if (length > 0) {
               ctx.addParameter(new Parameter(dataField).setType(Parameter.TYPE_ARRAY));

               for (int i = 0; i < length; i++) {
                  if (i > 0) {
                     sb.append(',');
                  }

                  sb.append('?');
               }
            } else {
               sb.append("null"); // to avoid SQL exception
            }

            return sb.toString();
         } else if (ctx.isWithinInToken() && value instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) value;
            Iterator<?> i = iterable.iterator();
            StringBuilder sb = new StringBuilder();

            if (i.hasNext()) {
               ctx.addParameter(new Parameter(dataField).setType(Parameter.TYPE_ITERABLE));

               for (; i.hasNext(); i.next()) {
                  if (sb.length() > 0) {
                     sb.append(',');
                  }

                  sb.append('?');
               }
            } else {
               sb.append("null"); // to avoid SQL exception
            }

            return sb.toString();
         } else {
            ctx.addParameter(new Parameter(dataField).setType(Parameter.TYPE_SINGLE_VALUE));
            return "?";
         }
      } else if (parameter.isInOut()) { // IN_OUT
         Variable variable = ctx.getEntityInfo().getVariable(dataField);

         ctx.addParameter(new Parameter(dataField, variable.sqlType(), variable.scale(), true).setType(Parameter.TYPE_SINGLE_VALUE));
         return "?";
      } else { // OUT
         Variable variable = ctx.getEntityInfo().getVariable(dataField);

         ctx.addParameter(new Parameter(dataField, variable.sqlType(), variable.scale()).setType(Parameter.TYPE_SINGLE_VALUE));
         return "?";
      }
   }
}
