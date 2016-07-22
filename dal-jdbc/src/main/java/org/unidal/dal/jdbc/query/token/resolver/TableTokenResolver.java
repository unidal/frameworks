package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.dal.jdbc.mapping.TableProviderManager;
import org.unidal.dal.jdbc.query.QueryNaming;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;TABLE [name="<i>table-name</i>"] [alias="<i>new-table-alias</i>"] /&gt;
 */
@Named(type = TokenResolver.class, value = "TABLE")
public class TableTokenResolver implements TokenResolver {
   @Inject
   private TableProviderManager m_manager;

   @Inject
   private QueryNaming m_naming;

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.TABLE) {
         throw new DalRuntimeException("Internal error: only TABLE token is supported by " + getClass());
      }

      SimpleTagToken table = (SimpleTagToken) token;
      String tableName = table.getAttribute("name", ctx.getEntityInfo().getLogicalName());
      String[] logicalNameAndAlias = ctx.getEntityInfo().getLogicalNameAndAlias(tableName);
      TableProvider tableProvider = m_manager.getTableProvider(logicalNameAndAlias[0]);
      String physicalTableName = tableProvider.getPhysicalTableName(ctx.getQueryHints(), tableName);

      switch (ctx.getQuery().getType()) {
      case SELECT:
         String alias = table.getAttribute("alias", logicalNameAndAlias[1]);

         ctx.setTableResolved(true);
         return m_naming.getTable(physicalTableName, alias);
      case INSERT:
         return m_naming.getTable(physicalTableName);
      case UPDATE:
         return m_naming.getTable(physicalTableName);
      case DELETE:
         return m_naming.getTable(physicalTableName);
      default:
         throw new DalRuntimeException("TABLE token does not support query type: " + ctx.getQuery().getType());
      }
   }
}
