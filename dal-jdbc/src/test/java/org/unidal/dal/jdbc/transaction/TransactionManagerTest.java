package org.unidal.dal.jdbc.transaction;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.datasource.DataSourceException;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

@Ignore
public class TransactionManagerTest extends ComponentTestCase {
   private void commitTransaction() throws Exception {
      TransactionManager tm = lookup(TransactionManager.class);

      tm.commitTransaction();
   }

   protected void delete(int id) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      queryEngine.deleteSingle(UserEntity.DELETE_BY_PK, proto);
   }

   protected void insert(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setUserId(id);
      proto.setUserName(userName);
      proto.setPassword("");

      queryEngine.insertSingle(UserEntity.INSERT, proto);
   }

   private void rollbackTransaction() throws Exception {
      TransactionManager tm = lookup(TransactionManager.class);

      tm.rollbackTransaction();
   }

   protected void select(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      User user = queryEngine.querySingle(UserEntity.FIND_BY_PK, proto, UserEntity.READSET_FULL);

      Assert.assertNotNull(user);
      Assert.assertEquals(proto.getKeyUserId(), user.getUserId());
      Assert.assertEquals(userName, user.getUserName());
      Assert.assertNotNull(user.getCreationDate());
      Assert.assertNotNull(user.getLastModifiedDate());
   }

   @Override
   public void setUp() throws Exception {
      super.setUp();

      EntityInfoManager entityManager = lookup(EntityInfoManager.class);

      entityManager.register(UserEntity.class);
   }

   private void startTransaction() throws Exception {
      TransactionManager tm = lookup(TransactionManager.class);

      tm.startTransaction("jdbc-dal");
   }

   @Override
   public void tearDown() throws Exception {

      super.tearDown();
   }

   @Test
   public void testNoTransaction() throws Exception {
      try {
         delete(1);
         insert(1, "user 1");
         select(1, "user 1");
         update(1, "user 11");
         select(1, "user 11");
         delete(1);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   @Test
   public void testTransactionCommit() throws Exception {
      try {
         delete(1);
         startTransaction();
         insert(1, "user 1");
         select(1, "user 1");
         update(1, "user 11");
         commitTransaction();
         select(1, "user 11");
         delete(1);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   @Test
   public void testTransactionRollback() throws Exception {
      try {
         delete(1);
         insert(1, "user 1");
         startTransaction();
         select(1, "user 1");
         update(1, "user 11");
         rollbackTransaction();
         select(1, "user 1");
         delete(1);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   protected void update(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);
      proto.setUserName(userName);

      queryEngine.updateSingle(UserEntity.UPDATE_BY_PK, proto, UserEntity.UPDATESET_FULL);
   }
}
