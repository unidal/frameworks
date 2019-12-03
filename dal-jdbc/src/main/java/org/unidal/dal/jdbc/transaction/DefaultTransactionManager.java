package org.unidal.dal.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.unidal.cat.Cat;
import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.dal.jdbc.mapping.TableProviderManager;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = TransactionManager.class)
public class DefaultTransactionManager implements TransactionManager, LogEnabled {
   private static ThreadLocalTransactionInfo m_threadLocalData = new ThreadLocalTransactionInfo();

   @Inject
   private TableProviderManager m_tableProviderManager;

   @Inject
   private DataSourceManager m_dataSourceManager;

   private Logger m_logger;

   public void closeConnection() {
      TransactionInfo trxInfo = m_threadLocalData.get();

      if (trxInfo.isInTransaction()) {
         // do nothing when in transaction
      } else {
         try {
            trxInfo.reset();
         } catch (SQLException e) {
            m_logger.warn("Error when closing Connection, message: " + e, e);
         }
      }
   }

   private void closeConnection(Connection connection) {
      if (connection != null) {
         try {
            connection.close();
         } catch (SQLException e) {
            // ignore it
         }
      }
   }

   public void commitTransaction() {
      TransactionInfo trxInfo = m_threadLocalData.get();

      if (!trxInfo.isInTransaction()) {
         throw new DalRuntimeException("There is no active transaction open, can't commit");
      }

      try {
         if (trxInfo.getConnection() != null) {
            trxInfo.getConnection().commit();
         }
      } catch (SQLException e) {
         throw new DalRuntimeException("Unable to commit transaction, message: " + e, e);
      } finally {
         try {
            trxInfo.reset();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public Connection getConnection(QueryContext ctx) {
      String logicalName = ctx.getEntityInfo().getLogicalName();
      TableProvider tableProvider = m_tableProviderManager.getTableProvider(logicalName);
      String dataSourceName = tableProvider.getDataSourceName(ctx.getQueryHints(), logicalName);
      TransactionInfo trxInfo = m_threadLocalData.get();

      ctx.setDataSourceName(dataSourceName);

      if (trxInfo.isInTransaction()) {
         if (dataSourceName.equals(trxInfo.getDataSourceName())) {
            return trxInfo.getConnection();
         } else {
            throw new DalRuntimeException("Only one datasource can participate in a transaction. Now: "
                  + trxInfo.getDataSourceName() + ", you provided: " + dataSourceName);
         }
      } else { // Not in transaction
         DataSource dataSource = m_dataSourceManager.getDataSource(dataSourceName);
         Connection connection = null;
         SQLException exception = null;

         try {
            connection = trxInfo.getConnection();

            if (connection == null) {
               connection = dataSource.getConnection();
            }

            connection.setAutoCommit(true);
         } catch (SQLException e) {
            exception = e;
         }

         // retry once if pooled connection is closed by server side
         if (exception != null) {
            closeConnection(connection);

            if (exception.getMessage().equals("An attempt by a client to checkout a Connection has timed out.")) {
               m_logger.warn(String.format("Database(%s) connection is timeout, try to reconnect.", dataSourceName));
            } else {
               m_logger.warn(String.format("Iffy database(%s) connection closed, try to reconnect.", dataSourceName), exception);
            }

            try {
               connection = dataSource.getConnection();
               connection.setAutoCommit(true);
               exception = null;
            } catch (SQLException e) {
               closeConnection(connection);
               m_logger.warn(String.format("Unable to reconnect to database(%s).", dataSourceName), e);
            }
         }

         if (exception != null) {
            throw new DalRuntimeException(
                  "Error when getting connection from DataSource(" + dataSourceName + "), message: " + exception, exception);
         } else {
            trxInfo.setConnection(connection);
            trxInfo.setDataSourceName(dataSourceName);
            trxInfo.setInTransaction(false);
            return connection;
         }
      }
   }

   public boolean isInTransaction() {
      TransactionInfo trxInfo = m_threadLocalData.get();

      return trxInfo.isInTransaction();
   }

   public void rollbackTransaction() {
      TransactionInfo trxInfo = m_threadLocalData.get();

      if (!trxInfo.isInTransaction()) {
         throw new DalRuntimeException("There is no active transaction open, can't rollback");
      }

      try {
         if (trxInfo.getConnection() != null) {
            trxInfo.getConnection().rollback();
         }
      } catch (SQLException e) {
         throw new DalRuntimeException("Unable to rollback transaction, message: " + e, e);
      } finally {
         try {
            trxInfo.reset();
         } catch (SQLException e) {
            Cat.logError(e);
         }
      }
   }

   public void startTransaction(String datasource) {
      TransactionInfo trxInfo = m_threadLocalData.get();

      if (trxInfo.isInTransaction()) {
         throw new DalRuntimeException("Can't start transaction while another transaction has not been committed or rollbacked!");
      } else {
         DataSource ds = m_dataSourceManager.getDataSource(datasource);
         Connection connection = null;

         try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            trxInfo.setConnection(connection);
            trxInfo.setDataSourceName(datasource);
            trxInfo.setInTransaction(true);
         } catch (SQLException e) {
            closeConnection(connection);
            throw new DalRuntimeException("Error when getting connection from DataSource(" + datasource + "), message: " + e, e);
         }
      }
   }

   static class ThreadLocalTransactionInfo extends ThreadLocal<TransactionInfo> {
      @Override
      protected TransactionInfo initialValue() {
         return new TransactionInfo();
      }
   }

   static class TransactionInfo {
      private String m_dataSourceName;

      private Connection m_connection;

      private boolean m_inTransaction;

      public Connection getConnection() {
         return m_connection;
      }

      public String getDataSourceName() {
         return m_dataSourceName;
      }

      public boolean isInTransaction() {
         try {
            if (m_connection != null && m_connection.isClosed()) {
               return false;
            }
         } catch (SQLException e) {
            Cat.logError(e);
         }

         return m_inTransaction;
      }

      public void reset() throws SQLException {
         if (m_connection != null) {
            m_connection.close();
         }

         m_connection = null;
         m_dataSourceName = null;
         m_inTransaction = false;
         m_threadLocalData.remove();
      }

      public void setConnection(Connection connection) {
         m_connection = connection;
      }

      public void setDataSourceName(String dataSourceName) {
         m_dataSourceName = dataSourceName;
      }

      public void setInTransaction(boolean inTransaction) {
         m_inTransaction = inTransaction;
      }
   }
}
