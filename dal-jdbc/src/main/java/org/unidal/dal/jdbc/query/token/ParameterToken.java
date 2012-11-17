package org.unidal.dal.jdbc.query.token;

public class ParameterToken implements Token {

   private String m_token;

   private boolean m_dollarSign;

   private boolean m_numberSign;

   public ParameterToken(String token, boolean hasDollarSign, boolean hasNumberSign) {
      m_token = token;
      m_dollarSign = hasDollarSign;
      m_numberSign = hasNumberSign;
   }

   public TokenType getType() {
      return TokenType.PARAM;
   }

   public String getParameterName() {
      return m_token;
   }

   public boolean isIn() {
      return m_dollarSign && !m_numberSign;
   }

   public boolean isOut() {
      return m_numberSign && !m_dollarSign;
   }

   public boolean isInOut() {
      return m_dollarSign && m_numberSign;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(32);

      if (m_dollarSign) {
         sb.append('$');
      } else if (m_numberSign) {
         sb.append('#');
      }

      sb.append('{').append(m_token).append('}');

      return sb.toString();
   }
}
