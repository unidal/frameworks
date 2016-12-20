package org.unidal.dal.jdbc.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.dal.jdbc.user.User;
import org.unidal.dal.jdbc.user.UserDao;
import org.unidal.dal.jdbc.user.UserEntity;

@Ignore
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
      User u = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals("Frankie", u.getUserName());

      User user = new User();

      for (int i = 10; i < 15; i++) {
         user.setUserId(i);
         user.setUserName("User " + i);

         dao.insert(user);
      }

      //showQuery("select * from user");
      //showQuery("show tables from INFORMATION_SCHEMA");
      //showQuery("select COLUMN_NAME,* from INFORMATION_SCHEMA.INDEXES where TABLE_NAME='USER'");

      dumpDeltaTo("user.xml", "user_delta.xml", "user");
   }

   @Test
   public void testClauseOnDuplicateKey() throws Exception {
      executeUpdate("insert into user(user_id,full_name,creation_date,last_modified_date) values(1,'full name',now(),now())");

      UserDao dao = lookup(UserDao.class);
      User u1 = dao.findByPK(1, UserEntity.READSET_FULL);

      executeUpdate("insert into user(user_id,full_name,creation_date,last_modified_date) values(1,'other name',now(),now())"
            + " on duplicate key update last_modified_date=now()");

      User u2 = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals(1, u2.getUserId());
      Assert.assertEquals("full name", u2.getUserName());
      Assert.assertEquals(u1.getCreationDate(), u2.getCreationDate());

      Assert.assertFalse(u1.getLastModifiedDate().equals(u2.getLastModifiedDate()));
   }

   @Test
   public void testFunctionPassword() throws Exception {
      executeUpdate("insert into user(user_id,full_name,creation_date,last_modified_date) values(1,password('full name'),now(),now())");

      UserDao dao = lookup(UserDao.class);
      User u = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals(1, u.getUserId());
      Assert.assertEquals(41, u.getUserName().length()); // not 9
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
//      Assert.assertEquals("Office address 1", userWithHomeOffice.getOfficeAddress().getAddress());
   }
}
