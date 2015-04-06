package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.lookup.annotation.Named;

@Named(type = TokenResolver.class, value = "STRING")
public class StringTokenResolver implements TokenResolver {
   public String resolve(Token token, QueryContext ctx) {
      return token.toString();
   }
}
