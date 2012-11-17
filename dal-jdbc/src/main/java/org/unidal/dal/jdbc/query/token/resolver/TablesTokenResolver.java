package org.unidal.dal.jdbc.query.token.resolver;

import java.util.HashSet;
import java.util.Set;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.annotation.Relation;
import org.unidal.dal.jdbc.annotation.SubObjects;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.dal.jdbc.mapping.TableProviderManager;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;

/**
 * &lt;tables /&gt;
 */
public class TablesTokenResolver implements TokenResolver {
   private TableProviderManager m_manager;

   private String getPhysicalName(QueryContext ctx, String logicalName) {
      TableProvider tableProvider = m_manager.getTableProvider(logicalName);
      String physicalTableName = tableProvider.getPhysicalTableName(ctx.getQueryHints());

      return physicalTableName;
   }

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.TABLES) {
         throw new DalRuntimeException("Internal error: only TABLES token is supported by " + getClass());
      }

      switch (ctx.getQuery().getType()) {
      case SELECT:
         EntityInfo entityInfo = ctx.getEntityInfo();
         SubObjects subobject = entityInfo.getSubobjects(ctx.getReadset());
         StringBuilder sb = new StringBuilder(256);

         sb.append(getPhysicalName(ctx, entityInfo.getLogicalName())).append(' ').append(entityInfo.getAlias());

         if (subobject != null) {
            String[] names = subobject.value();
            Set<String> done = new HashSet<String>();

            done.add(entityInfo.getLogicalName());

            for (String name : names) {
               if (name != null && name.length() > 0) {
                  // only include a table once
                  if (done.contains(name)) {
                     continue;
                  } else {
                     done.add(name);
                  }

                  Relation relation = entityInfo.getRelation(name);

                  sb.append(", ").append(getPhysicalName(ctx, relation.logicalName()));
                  sb.append(' ').append(relation.alias());
               }
            }
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
