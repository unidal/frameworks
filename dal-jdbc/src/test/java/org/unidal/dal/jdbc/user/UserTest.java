package org.unidal.dal.jdbc.user;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.dal.jdbc.test.JdbcTestCase;

public class UserTest extends JdbcTestCase {
   @Before
   public void before() throws Exception {
      super.createTables("user");
   }

   @Override
   protected String getDefaultDataSource() {
      return "user";
   }

   @Test
   public void testUser() throws Exception {
      //      executeUpdate("insert into user(user_id, full_name, creation_date, last_modified_date) values (1, 'Frankie', now(), now())");
      //      executeUpdate("insert into user(user_id, full_name, creation_date, last_modified_date) values (2, 'Daniel', now(), now())");
      //      executeUpdate("insert into user(user_id, full_name, creation_date, last_modified_date) values (3, 'Bob', now(), now())");

      loadFrom("user.xml");

      showQuery("select * from user");

      // dumpTo(new File("src/test/resources/org/unidal/dal/jdbc/user/user.xml"), "user");

      UserDao dao = lookup(UserDao.class);
      User user = dao.findByPK(1, UserEntity.READSET_FULL);

      Assert.assertEquals("Frankie", user.getFullName());
   }
}
