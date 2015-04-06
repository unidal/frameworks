package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;value name="<i>field-name</i>" /&gt;
 */
@Named(type = TokenResolver.class, value = "VALUE")
public class ValueTokenResolver implements TokenResolver {
   @Inject
   private ExpressionResolver m_expressionResolver;
   
   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.VALUE) {
         throw new DalRuntimeException("Internal error: only VALUE token is supported by " + getClass());
      }

      EntityInfo entityInfo = ctx.getEntityInfo();
      StringBuilder sb = new StringBuilder(1024);
      String fieldName = ((SimpleTagToken) token).getAttribute("name", null);
      DataField field = ctx.getEntityInfo().getFieldByName(fieldName);

      if (field != null) {
         switch (ctx.getQuery().getType()) {
         case SELECT:
            throw new DalRuntimeException("VALUE token does not support query type: " + ctx.getQuery().getType());
         case INSERT:
            DataObject proto = ctx.getProto();
            Attribute attribute = entityInfo.getAttribute(field);

            if (!proto.isFieldUsed(field) && attribute.insertExpr().length() > 0) {
               sb.append(m_expressionResolver.resolve(ctx, attribute.insertExpr()));
            } else {
               ctx.addParameter(new Parameter(field));
               sb.append('?');
            }

            break;
         case UPDATE:
            throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
         case DELETE:
            throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
         default:
            throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
         }
      } else {
         throw new DalRuntimeException("DataField(" + fieldName + ") is not defined in "
               + ctx.getQuery().getEntityClass() + ". Query: " + ctx.getQuery());
      }

      return sb.toString();
   }
}
