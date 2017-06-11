package org.unidal.dal.jdbc.intg;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.datasource.DataSourceException;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.user.address.dal.UserAddress;
import org.unidal.test.user.address.dal.UserAddressEntity;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

public class MultipleTablesTest extends ComponentTestCase {
   private EntityInfoManager m_entityManager;

   protected void deleteUser(int id) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      queryEngine.deleteSingle(UserEntity.DELETE_BY_PK, proto);
   }

   protected void deleteUserAddresses(int id) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      UserAddress proto = new UserAddress();

      proto.setKeyUserId(id);

      queryEngine.deleteSingle(UserAddressEntity.DELETE_ALL_BY_USER_ID, proto);
   }

   protected void deleteUserAddress(int id, String type) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      UserAddress proto = new UserAddress();

      proto.setKeyUserId(id);
      proto.setKeyType(type);

      queryEngine.deleteSingle(UserAddressEntity.DELETE_BY_PK, proto);
   }

   protected void insertUser(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setUserId(id);
      proto.setUserName(userName);
      proto.setPassword("");

      queryEngine.insertSingle(UserEntity.INSERT, proto);
   }

   protected void insertUserAddress(int id, String type, String address) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      UserAddress proto = new UserAddress();

      proto.setUserId(id);
      proto.setType(type);
      proto.setAddress(address);

      queryEngine.insertSingle(UserAddressEntity.INSERT, proto);
   }

   protected void selectUser(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      User user = queryEngine.querySingle(UserEntity.FIND_BY_PK, proto, UserEntity.READSET_FULL);

      Assert.assertNotNull(user);
      Assert.assertEquals(id, user.getUserId());
      Assert.assertEquals(userName, user.getUserName());
      Assert.assertNotNull(user.getCreationDate());
      Assert.assertNotNull(user.getLastModifiedDate());
   }

   protected void selectUserAndAddress(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      User user = queryEngine.querySingle(UserEntity.FIND_WITH_SUBOBJECTS_BY_PK, proto,
            UserEntity.READSET_FULL_WITH_HOME_OFFICE_ADDRESS_FULL);

      Assert.assertNotNull(user);
      Assert.assertEquals(id, user.getUserId());
      Assert.assertEquals(userName, user.getUserName());
      Assert.assertNotNull(user.getCreationDate());
      Assert.assertNotNull(user.getLastModifiedDate());
      Assert.assertNotNull(user.getHomeAddress());
      Assert.assertNotNull(user.getOfficeAddress());
      Assert.assertNull(user.getBillingAddress());
   }

   protected void selectUserAddress(int id, String type, String address) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      UserAddress proto = new UserAddress();

      proto.setKeyUserId(id);
      proto.setKeyType(type);

      UserAddress userAddress = queryEngine.querySingle(UserAddressEntity.FIND_BY_PK, proto,
            UserAddressEntity.READSET_FULL);

      Assert.assertNotNull(userAddress);
      Assert.assertEquals(id, userAddress.getUserId());
      Assert.assertEquals(type, userAddress.getType());
      Assert.assertEquals(address, userAddress.getAddress());
   }

   @Override
   public void setUp() throws Exception {
      super.setUp();

      m_entityManager = lookup(EntityInfoManager.class);

      m_entityManager.register(UserEntity.class);
      m_entityManager.register(UserAddressEntity.class);
   }

   @Test
   @Ignore
   public void testSingle() throws Exception {
      try {
         deleteUser(1);
         deleteUserAddresses(1);
         insertUser(1, "user 1");
         insertUserAddress(1, "H", "home address 1");
         insertUserAddress(1, "B", "billing address 1");
         insertUserAddress(1, "O", "office address 1");
         selectUserAndAddress(1, "user 1");
         updateUser(1, "user 11");
         updateUserAddress(1, "H", "home address 11");
         selectUser(1, "user 11");
         selectUserAddress(1, "H", "home address 11");
         selectUserAddress(1, "B", "billing address 1");
         selectUserAddress(1, "O", "office address 1");
         selectUserAndAddress(1, "user 11");
         deleteUser(1);
         deleteUserAddresses(1);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   protected void updateUser(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);
      proto.setUserName(userName);

      queryEngine.updateSingle(UserEntity.UPDATE_BY_PK, proto, UserEntity.UPDATESET_FULL);
   }

   protected void updateUserAddress(int id, String type, String newAddress) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      UserAddress proto = new UserAddress();

      proto.setKeyUserId(id);
      proto.setKeyType(type);
      proto.setAddress(newAddress);

      queryEngine.updateSingle(UserAddressEntity.UPDATE_BY_PK, proto, UserAddressEntity.UPDATESET_FULL);
   }
}
