package org.unidal.dal.jdbc.query;

public interface QueryNaming {
   public String getTable(String table);

   public String getField(String field);

   public String getField(String field, String tableAlias);

   public String getTable(String table, String alias);
}
