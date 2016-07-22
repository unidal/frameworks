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
import org.unidal.dal.jdbc.query.QueryNaming;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;TABLES /&gt;
 */
@Named(type = TokenResolver.class, value = "TABLES")
public class TablesTokenResolver implements TokenResolver {
   @Inject
   private TableProviderManager m_manager;

   @Inject
   private QueryNaming m_naming;

   private String getPhysicalName(QueryContext ctx, String logicalName) {
      TableProvider tableProvider = m_manager.getTableProvider(logicalName);
      String physicalTableName = tableProvider.getPhysicalTableName(ctx.getQueryHints(), logicalName);

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
         String table = getPhysicalName(ctx, entityInfo.getLogicalName());

         sb.append(m_naming.getTable(table, entityInfo.getAlias()));

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

                  sb.append(", ");

                  sb.append(m_naming.getTable(getPhysicalName(ctx, relation.logicalName()), relation.alias()));
               }
            }
         }

         ctx.setTableResolved(true);
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
