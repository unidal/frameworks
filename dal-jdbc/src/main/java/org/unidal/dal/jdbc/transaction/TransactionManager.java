package org.unidal.dal.jdbc.transaction;

import java.sql.Connection;

import org.unidal.dal.jdbc.engine.QueryContext;

public interface TransactionManager {
   public void closeConnection();

   public void commitTransaction();

   public Connection getConnection(QueryContext ctx);

   public boolean isInTransaction();

   public void rollbackTransaction();

   public void startTransaction(String datasource);
}
