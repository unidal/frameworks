package org.unidal.dal.jdbc.query.token;

public class EndTagToken implements Token {
   private String m_token;

   private TokenType m_tag;

   public EndTagToken(String token) {
      m_token = token;
      m_tag = TokenType.valueOf(token);
   }

   public TokenType getType() {
      return m_tag;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(32);

      sb.append("</").append(m_token).append('>');
      return sb.toString();
   }
}
