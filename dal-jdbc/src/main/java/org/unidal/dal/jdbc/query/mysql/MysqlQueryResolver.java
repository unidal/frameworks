package org.unidal.dal.jdbc.query.mysql;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.query.QueryResolver;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenParser;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.dal.jdbc.query.token.resolver.TokenResolver;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;

@Named(type = QueryResolver.class)
public class MysqlQueryResolver extends ContainerHolder implements QueryResolver, Initializable {
   @Inject
   private TokenParser m_tokenParser;

   private Map<String, TokenResolver> m_map;

   public void initialize() throws InitializationException {
      m_map = lookupMap(TokenResolver.class);
   }

   public void resolve(QueryContext ctx) {
      List<Token> tokens = ctx.getQuery().parse(m_tokenParser);
      StringBuilder sb = new StringBuilder(1024);

      for (Token token : tokens) {
         TokenType type = token.getType();
         TokenResolver resolver = m_map.get(type.name());

         if (resolver == null) {
            throw new DalRuntimeException("No TokenResolver registered for token type (" + token + ")");
         }

         if (type == TokenType.IF) {
            sb.append(resolver.resolve(token, ctx));
         } else {
            if (!ctx.isSqlResolveDisabled()) {
               sb.append(resolver.resolve(token, ctx));
            }
         }
      }

      // add tag <FIELDS/> on the fly for Store Procedure Query
      if (ctx.getQuery().isStoreProcedure() && ctx.getQuery().getType() == QueryType.SELECT
            && ctx.getOutFields().isEmpty()) {
         TokenResolver resolver = m_map.get(TokenType.FIELDS.name());
         Map<String, String> attributes = Collections.emptyMap();

         resolver.resolve(new SimpleTagToken("FIELDS", attributes), ctx);
      }

      ctx.setSqlStatement(sb.toString());
   }
}
