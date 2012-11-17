package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.query.token.EndTagToken;
import org.unidal.dal.jdbc.query.token.StartTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;

/**
 * &lt;IF type="<i>NOT_NULL</i>|<i>NOT_ZERO</i>|<i>ZERO</i>|<i>EQ</i>"
 * field="<i>field-name</i>" [value="<i>value</i>"] &gt;...&lt/IF&gt;
 */
public class IfTokenResolver implements TokenResolver {
   private DataObjectAccessor m_accessor;

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.IF) {
         throw new DalRuntimeException("Internal error: only IF token is supported by " + getClass());
      }

      if (token instanceof StartTagToken) {
         if (ctx.isWithinIfToken()) {
            throw new DalRuntimeException("IF token can't be nested");
         }

         ctx.setWithinIfToken(true);
         ctx.setSqlResolveDisabled(!evaluate((StartTagToken) token, ctx));
         return "";
      } else if (token instanceof EndTagToken) {
         ctx.setWithinIfToken(false);
         ctx.setSqlResolveDisabled(false);
         return "";
      } else {
         throw new DalRuntimeException("Internal error: IF token can only be used as <IF ...> or </IF>");
      }
   }

   private boolean evaluate(StartTagToken token, QueryContext ctx) {
      String type = token.getAttribute("type", "");
      String fieldName = token.getAttribute("field", "");
      DataField dataField = ctx.getEntityInfo().getFieldByName(fieldName);
      DataObject proto = ctx.getProto();
      Object fieldValue = m_accessor.getFieldValue(proto, dataField);

      if ("NOT_NULL".equalsIgnoreCase(type)) {
         return proto.isFieldUsed(dataField) && fieldValue != null;
      } else if ("NOT_ZERO".equalsIgnoreCase(type)) {
         if (fieldValue == null) {
            return false;
         } else {
            return isNotZero(fieldValue);
         }
      } else if ("ZERO".equalsIgnoreCase(type)) {
         if (fieldValue == null) {
            return false;
         } else {
            return !isNotZero(fieldValue);
         }
      } else if ("EQ".equalsIgnoreCase(type)) {
         String value = token.getAttribute("value", null);

         if (value == null) {
            throw new DalRuntimeException("Internal error: IF token with EQ type must have a value attribute.");
         }

         return value.equals(String.valueOf(fieldValue));
      } else {
         throw new DalRuntimeException("Unsupported type:" + type + ", please use NOT_NULL or NOT_ZERO or ZERO instead");
      }
   }

   private boolean isNotZero(Object value) {
      if (value instanceof Boolean) {
         return ((Boolean) value).booleanValue();
      } else if (value instanceof Double) {
         return Math.abs(((Double) value).doubleValue()) > 1e-6;
      } else {
         return !"0".equals(String.valueOf(value));
      }
   }
}
