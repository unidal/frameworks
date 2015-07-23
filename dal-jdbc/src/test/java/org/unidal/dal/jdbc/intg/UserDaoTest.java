package org.unidal.dal.jdbc.intg;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.dal.jdbc.datasource.DataSourceException;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.test.JdbcTestCase;
import org.unidal.test.user.address.dal.UserAddress;
import org.unidal.test.user.address.dal.UserAddressDao;
import org.unidal.test.user.address.dal.UserAddressEntity;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserDao;
import org.unidal.test.user.dal.UserEntity;

public class UserDaoTest extends JdbcTestCase {
   private EntityInfoManager m_entityManager;

   @Override
   public void setUp() throws Exception {
      super.setUp();

      m_entityManager = lookup(EntityInfoManager.class);

      m_entityManager.register(UserEntity.class);
      m_entityManager.register(UserAddressEntity.class);
   }

   @Test
   public void testFindByPK() throws Exception {
      super.createTables("user");
      
      try {
         UserDao userDao = lookup(UserDao.class);
         UserAddressDao userAddressDao = lookup(UserAddressDao.class);
         User user1 = userDao.createLocal();

         user1.setKeyUserId(1);
         userDao.delete(user1);
         userAddressDao.deleteAllByUserId(1);

         user1.setUserId(1);
         user1.setUserName("user name");
         user1.setPassword("");
         userDao.insert(user1);

         User user2 = userDao.findByPK(1, UserEntity.READSET_FULL);

         Assert.assertEquals(user2.getUserId(), user1.getUserId());
         Assert.assertEquals(user2.getUserName(), user1.getUserName());

         UserAddress userAddress1 = userAddressDao.createLocal();

         userAddress1.setUserId(1);
         userAddress1.setType("H");
         userAddress1.setAddress("Home Address");
         userAddressDao.insert(userAddress1);

         userAddress1.setUserId(1);
         userAddress1.setType("O");
         userAddress1.setAddress("Office Address");
         userAddressDao.insert(userAddress1);

         userAddress1.setUserId(1);
         userAddress1.setType("B");
         userAddress1.setAddress("Billing Address");
         userAddressDao.insert(userAddress1);

         User user3 = userDao.findWithSubObjectsByPK(1, UserEntity.READSET_FULL_WITH_ALL_ADDRESSES_FULL);
         Assert.assertEquals("H", user3.getHomeAddress().getType());
         Assert.assertEquals("Home Address", user3.getHomeAddress().getAddress());
         Assert.assertEquals("O", user3.getOfficeAddress().getType());
         Assert.assertEquals("Office Address", user3.getOfficeAddress().getAddress());
         Assert.assertEquals("B", user3.getBillingAddress().getType());
         Assert.assertEquals("Billing Address", user3.getBillingAddress().getAddress());

         userDao.delete(user1);
         userAddressDao.deleteAllByUserId(1);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   @Override
   protected String getDefaultDataSource() {
      return "jdbc-dal";
   }
}
