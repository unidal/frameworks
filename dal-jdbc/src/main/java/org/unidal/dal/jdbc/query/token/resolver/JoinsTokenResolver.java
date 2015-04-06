package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.annotation.Relation;
import org.unidal.dal.jdbc.annotation.SubObjects;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;JOINS /&gt;
 */
@Named(type = TokenResolver.class, value = "JOINS")
public class JoinsTokenResolver implements TokenResolver {
   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.JOINS) {
         throw new DalRuntimeException("Internal error: only JOINS token is supported by " + getClass());
      }

      switch (ctx.getQuery().getType()) {
      case SELECT:
         EntityInfo entityInfo = ctx.getEntityInfo();
         SubObjects subobject = entityInfo.getSubobjects(ctx.getReadset());
         StringBuilder sb = new StringBuilder(256);

         if (subobject != null) {
            String[] names = subobject.value();

            for (String name : names) {
               if (name != null && name.length() > 0) {
                  Relation relation = entityInfo.getRelation(name);

                  if (sb.length() > 0) {
                     sb.append(" and ");
                  }

                  sb.append(relation.join());
               }
            }
         }

         if (sb.length() == 0) {
            sb.append("1=1");
         }

         return sb.toString();
      case INSERT:
         throw new DalRuntimeException("TABLES token does not support query type: " + ctx.getQuery().getType());
      case UPDATE:
         throw new DalRuntimeException("TABLES token does not support query type: " + ctx.getQuery().getType());
      case DELETE:
         throw new DalRuntimeException("TABLES token does not support query type: " + ctx.getQuery().getType());
      default:
         throw new DalRuntimeException("TABLES token does not support query type: " + ctx.getQuery().getType());
      }
   }
}
