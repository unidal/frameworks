package org.unidal.dal.jdbc.query.token.resolver;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.query.token.EndTagToken;
import org.unidal.dal.jdbc.query.token.StartTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;IN&gt;...&lt;/IN&gt;
 */
@Named(type = TokenResolver.class, value = "IN")
public class InTokenResolver implements TokenResolver {
   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.IN) {
         throw new DalRuntimeException("Internal error: only IN token is supported by " + getClass());
      }

      if (token instanceof StartTagToken) {
         if (ctx.isWithinInToken()) {
            throw new DalRuntimeException("IN token can't be nested");
         }

         ctx.setWithinInToken(true);
         return "(";
      } else if (token instanceof EndTagToken) {
         ctx.setWithinInToken(false);
         return ")";
      } else {
         throw new DalRuntimeException("Internal error: IN token can only be used as <IN> or </IN>");
      }
   }
}
