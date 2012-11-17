package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.query.token.Token;

public class StringTokenResolver implements TokenResolver {
   public String resolve(Token token, QueryContext ctx) {
      return token.toString();
   }
}
