package org.unidal.dal.jdbc.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.dal.jdbc.user.User;
import org.unidal.dal.jdbc.user.UserDao;
import org.unidal.dal.jdbc.user.UserEntity;

public class UserTest extends JdbcTestCase {
   @Before
   public void before() throws Exception {
      createTables("user");
   }

   @Override
   protected String getDefaultDataSource() {
      return "user";
   }

   @Test
   public void testUser() throws Exception {
      loadFrom("user.xml");

      UserDao dao = lookup(UserDao.class);
      User user = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals("Frankie", user.getUserName());
   }

   @Test
   public void testUserWithHomeAddress() throws Exception {
      loadFrom("user.xml");

      UserDao dao = lookup(UserDao.class);
      User userWithHome = dao.findByPK(1, UserEntity.READSET_FULL_HOME);

      Assert.assertEquals("Frankie", userWithHome.getUserName());
      Assert.assertNotNull(userWithHome.getHomeAddress());
      Assert.assertNull(userWithHome.getOfficeAddress());
      Assert.assertNull(userWithHome.getBillingAddress());

      Assert.assertEquals("Home address 1", userWithHome.getHomeAddress().getAddress());
   }

   @Test
   public void testUserWithHomeAndOfficeAddress() throws Exception {
      loadFrom("user.xml");

      UserDao dao = lookup(UserDao.class);
      User userWithHomeOffice = dao.findByPK(1, UserEntity.READSET_FULL_HOME_OFFICE);

      Assert.assertEquals("Frankie", userWithHomeOffice.getUserName());
      Assert.assertNotNull(userWithHomeOffice.getHomeAddress());
      Assert.assertNotNull(userWithHomeOffice.getOfficeAddress());
      Assert.assertNull(userWithHomeOffice.getBillingAddress());

      Assert.assertEquals("Home address 1", userWithHomeOffice.getHomeAddress().getAddress());
      Assert.assertEquals("Office address 1", userWithHomeOffice.getOfficeAddress().getAddress());
   }
}
