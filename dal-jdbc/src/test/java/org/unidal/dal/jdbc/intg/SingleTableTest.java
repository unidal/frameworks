package org.unidal.dal.jdbc.intg;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.datasource.DataSourceException;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.mapping.SimpleTableProvider;
import org.unidal.dal.jdbc.test.JdbcTestCase;
import org.unidal.test.user.address.dal.UserAddressEntity;
import org.unidal.test.user.dal.User;
import org.unidal.test.user.dal.UserEntity;

@Ignore
public class SingleTableTest extends JdbcTestCase {
   @Before
   public void before() throws Exception {
      define(SimpleTableProvider.class, "user") //
            .config("data-source-name", "user") //
            .config("physical-table-name", "user");

      EntityInfoManager manager = lookup(EntityInfoManager.class);

      manager.register(UserEntity.class);
      manager.register(UserAddressEntity.class);

      executeUpdate("create table user(user_id int primary key, full_name varchar(30), encrypted_password varchar(30), creation_date datetime default now(), last_modified_date timestamp default now())");
      executeUpdate("insert into user(user_id, full_name) values (1, 'frankie')");

      select(1, "frankie");
   }

   protected void delete(int id) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      queryEngine.deleteSingle(UserEntity.DELETE_BY_PK, proto);
   }

   @Override
   protected String getDefaultDataSource() {
      return "user";
   }

   protected void insert(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setUserId(id);
      proto.setUserName(userName);

      queryEngine.insertSingle(UserEntity.INSERT, proto);
   }

   protected void select(int id, String expectedUserName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);

      User user = queryEngine.querySingle(UserEntity.FIND_BY_PK, proto, UserEntity.READSET_FULL);

      Assert.assertNotNull(user);
      Assert.assertEquals(proto.getKeyUserId(), user.getUserId());
      Assert.assertEquals(expectedUserName, user.getUserName());
      Assert.assertNotNull(user.getCreationDate());
      Assert.assertNotNull(user.getLastModifiedDate());
   }

   protected void selectWithStoreProcedure(int count) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setPageSize(count);
      List<User> users = queryEngine.queryMultiple(UserEntity.FIND_ALL_USERS, proto, UserEntity.READSET_COMPACT);

      Assert.assertNotNull(users);
      Assert.assertEquals(2, users.size());
      Assert.assertEquals(3, proto.getPageSize());
   }

   @Test
   public void testMultiple() throws Exception {
      try {
         delete(1);
         delete(2);
         delete(3);
         insert(1, "user 1");
         insert(2, "user 2");
         insert(3, "user 3");
         update(1, "user 11");
         update(3, "user 31");
         select(1, "user 11");
         select(2, "user 2");
         select(3, "user 31");
         // TODO remove temporary
         // selectWithStoreProcedure(3);
         delete(1);
         delete(2);
         delete(3);
      } catch (DataSourceException e) {
         if (e.isDataSourceDown()) {
            System.out.println("Can't connect to database, gave up");
         } else {
            throw e;
         }
      }
   }

   @Test
   public void testSingle() throws Exception {
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

   protected void update(int id, String userName) throws Exception {
      QueryEngine queryEngine = lookup(QueryEngine.class);
      User proto = new User();

      proto.setKeyUserId(id);
      proto.setUserName(userName);

      queryEngine.updateSingle(UserEntity.UPDATE_BY_PK, proto, UserEntity.UPDATESET_FULL);
   }
}
