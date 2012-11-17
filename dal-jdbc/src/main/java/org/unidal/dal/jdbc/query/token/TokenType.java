package org.unidal.dal.jdbc.query.token;

import java.util.Arrays;
import java.util.List;

public enum TokenType {
   TABLES,

   TABLE,

   FIELDS,

   FIELD,

   VALUES,
   
   JOINS,

   IN,

   IF("type", "field", "value"),
   
   STRING,
   
   PARAM,
   
   VALUE,

   ;

   private List<String> m_supportAttributes;

   private TokenType(String... supportAttributes) {
      m_supportAttributes = Arrays.asList(supportAttributes);
   }

   public List<String> getSupportAttributes() {
      return m_supportAttributes;
   }
}
