package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;VALUES /&gt;
 */
@Named(type = TokenResolver.class, value = "VALUES")
public class ValuesTokenResolver implements TokenResolver {
   @Inject
   private ExpressionResolver m_expressionResolver;
   
   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.VALUES) {
         throw new DalRuntimeException("Internal error: only VALUES token is supported by " + getClass());
      }

      EntityInfo entityInfo = ctx.getEntityInfo();
      StringBuilder sb = new StringBuilder(1024);

      switch (ctx.getQuery().getType()) {
      case SELECT:
         throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
      case INSERT:
         DataObject proto = ctx.getProto();
         
         for (DataField field : entityInfo.getAttributeFields()) {
            Attribute attribute = entityInfo.getAttribute(field);

            if (attribute != null) {
               if (attribute.field().length() > 0 && !(attribute.autoIncrement() && !proto.isFieldUsed(field))) {
                  if (sb.length() > 0) {
                     sb.append(',');
                  }

                  if (!proto.isFieldUsed(field) && attribute.insertExpr().length() > 0) {
                     sb.append(m_expressionResolver.resolve(ctx, attribute.insertExpr()));
                  } else {
                     sb.append('?');
                     ctx.addParameter(new Parameter(field));
                  }
               }
            } else {
               throw new DalRuntimeException("Internal error: No Attribute annotation defined for field: " + field);
            }
         }

         break;
      case UPDATE:
         throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
      case DELETE:
         throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
      default:
         throw new DalRuntimeException("VALUES token does not support query type: " + ctx.getQuery().getType());
      }

      return sb.toString();
   }
}
