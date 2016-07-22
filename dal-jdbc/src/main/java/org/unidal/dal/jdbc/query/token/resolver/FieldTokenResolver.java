package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.query.QueryNaming;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;FIELD name="<i>field-name</i>" [table="<i>table-name</i>"] /&gt;
 */
@Named(type = TokenResolver.class, value = "FIELD")
public class FieldTokenResolver implements TokenResolver {
   @Inject
   private EntityInfoManager m_manager;

   @Inject
   private ExpressionResolver m_expressionResolver;

   @Inject
   private QueryNaming m_naming;

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.FIELD) {
         throw new DalRuntimeException("Internal error: only FIELD token is supported by " + getClass());
      }

      SimpleTagToken field = (SimpleTagToken) token;
      String fieldName = field.getAttribute("name", null);
      String tableName = field.getAttribute("table", ctx.getEntityInfo().getLogicalName());
      String[] logicalNameAndAlias = ctx.getEntityInfo().getLogicalNameAndAlias(tableName);
      EntityInfo entityInfo = m_manager.getEntityInfo(logicalNameAndAlias[0]);
      Attribute attribute = entityInfo.getAttribute(fieldName);

      if (attribute != null) {
         switch (ctx.getQuery().getType()) {
         case SELECT:
            if (!ctx.isTableResolved()) {
               DataField f = entityInfo.getFieldByName(fieldName);

               ctx.addOutField(f);
               ctx.addOutSubObjectName(null);
            }

            if (attribute.selectExpr().length() > 0) {
               return m_expressionResolver.resolve(ctx, attribute.selectExpr());
            } else {
               String tableAlias = logicalNameAndAlias[1];

               return m_naming.getField(attribute.field(), tableAlias);
            }
         case INSERT:
            return m_naming.getField(attribute.field());
         case UPDATE:
            return m_naming.getField(attribute.field());
         case DELETE:
            return m_naming.getField(attribute.field());
         default:
            throw new DalRuntimeException("TABLE token does not support query type: " + ctx.getQuery().getType());
         }
      } else {
         throw new DalRuntimeException("DataField(" + fieldName + ") is not defined in "
               + ctx.getQuery().getEntityClass() + ". Query: " + ctx.getQuery());
      }
   }
}
