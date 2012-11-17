package org.unidal.dal.jdbc.query.token;

public final class StringToken implements Token {
   private String m_token;

   public StringToken(String token) {
      m_token = token;
   }

   public TokenType getType() {
      return TokenType.STRING;
   }

   @Override
   public String toString() {
      return m_token;
   }
}