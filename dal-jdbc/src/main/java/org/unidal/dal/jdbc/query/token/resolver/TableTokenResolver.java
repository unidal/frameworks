package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.dal.jdbc.mapping.TableProviderManager;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;

/**
 * &lt;table [name="<i>table-name</i>"] [alias="<i>new-table-alias</i>"] /&gt;
 */
public class TableTokenResolver implements TokenResolver {
   private TableProviderManager m_manager;

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.TABLE) {
         throw new DalRuntimeException("Internal error: only TABLE token is supported by " + getClass());
      }

      SimpleTagToken table = (SimpleTagToken) token;
      String tableName = table.getAttribute("name", ctx.getEntityInfo().getLogicalName());
      String[] logicalNameAndAlias = ctx.getEntityInfo().getLogicalNameAndAlias(tableName);
      TableProvider tableProvider = m_manager.getTableProvider(logicalNameAndAlias[0]);
      String physicalTableName = tableProvider.getPhysicalTableName(ctx.getQueryHints());
      String quotedTableName = "`" + physicalTableName + "`";

      switch (ctx.getQuery().getType()) {
      case SELECT:
         String alias = table.getAttribute("alias", logicalNameAndAlias[1]);

         return quotedTableName + " " + alias;
      case INSERT:
         return quotedTableName;
      case UPDATE:
         return quotedTableName;
      case DELETE:
         return quotedTableName;
      default:
         throw new DalRuntimeException("TABLE token does not support query type: " + ctx.getQuery().getType());
      }
   }
}
