package org.unidal.dal.jdbc.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.dal.jdbc.test.JdbcTestCase;
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

      // dumpTo("user.xml", "user");

      showQuery("select * from user");
      showQuery("select * from user_address");

      UserDao dao = lookup(UserDao.class);
      User user = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals("Frankie", user.getUserName());
   }
}
