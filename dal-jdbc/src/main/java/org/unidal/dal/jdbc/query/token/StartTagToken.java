package org.unidal.dal.jdbc.query.token;

import java.util.LinkedHashMap;
import java.util.Map;

public class StartTagToken implements Token {
   private String m_token;

   private Map<String, String> m_attributes;

   private TokenType m_tag;

   public StartTagToken(String token, Map<String, String> attributes) {
      m_token = token;
      m_attributes = new LinkedHashMap<String, String>(attributes);
      m_tag = TokenType.valueOf(token);
   }

   public TokenType getType() {
      return m_tag;
   }

   public String getAttribute(String name, String defaultValue) {
      String value = m_attributes.get(name);

      if (value == null) {
         return defaultValue;
      } else {
         return value;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(256);

      sb.append('<').append(m_token);

      for (Map.Entry<String, String> entry : m_attributes.entrySet()) {
         sb.append(' ').append(entry.getKey()).append("='").append(entry.getValue()).append('\'');
      }

      sb.append('>');
      return sb.toString();
   }
}
