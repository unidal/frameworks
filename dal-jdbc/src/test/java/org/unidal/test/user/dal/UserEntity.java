package org.unidal.test.user.dal;

import java.sql.Types;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.annotation.Relation;
import org.unidal.dal.jdbc.annotation.SubObjects;
import org.unidal.dal.jdbc.annotation.Variable;
import org.unidal.test.user.address.dal.UserAddressEntity;

@Entity(logicalName = "user", alias = "u")
public final class UserEntity {

   @Relation(logicalName = "user-address", alias = "hua", join = "u.user_id=hua.user_id and hua.type='H'")
   public static DataField HOME_ADDRESS = new DataField("home-address");

   @Relation(logicalName = "user-address", alias = "oua", join = "u.user_id=oua.user_id and oua.type='O'")
   public static DataField OFFICE_ADDRESS = new DataField("office-address");

   @Relation(logicalName = "user-address", alias = "bua", join = "u.user_id=bua.user_id and bua.type='B'")
   public static DataField BILLING_ADDRESS = new DataField("billing-address");

   @Attribute(field = "user_id", nullable = false, primaryKey = true, autoIncrement = true)
   public static DataField USER_ID = new DataField("user-id");

   @Attribute(field = "full_name", nullable = false)
   public static DataField USER_NAME = new DataField("user-name");

   @Attribute(field = "creation_date", insertExpr = "NOW()")
   public static DataField CREATION_DATE = new DataField("creation-date");

   @Attribute(field = "last_modified_date", insertExpr = "NOW()", updateExpr = "NOW()")
   public static DataField LAST_MODIFIED_DATE = new DataField("last-modified-date");

   @Attribute(field = "", selectExpr = "upper(full_name)")
   public static DataField UPPER_USER_NAME = new DataField("upper-user-name");
   
   @Attribute(field = "encrypted_password", insertExpr = "password(${password})", updateExpr = "password(${password})")
   public static DataField ENCRYPTED_PASSWORD = new DataField("encrypted-password");

   @Variable
   public static DataField KEY_USER_ID = new DataField("key-user-id");

   @Variable
   public static DataField USER_ID_ARRAY = new DataField("user-id-array");
   
   @Variable
   public static DataField USER_ID_LIST = new DataField("user-id-list");

   @Variable(sqlType = Types.INTEGER)
   public static DataField PAGE_SIZE = new DataField("page-size");
   
   @Variable
   public static DataField PASSWORD = new DataField("password");

   public static Readset<User> READSET_COMPACT = new Readset<User>(USER_ID, USER_NAME);

   public static Readset<User> READSET_FULL = new Readset<User>(USER_ID, USER_NAME, CREATION_DATE, LAST_MODIFIED_DATE);
   
   public static Readset<User> READSET_U = new Readset<User>(UPPER_USER_NAME);
   
   @SubObjects( { "", "" })
   public static Readset<User> READSET_FULL_U = new Readset<User>(READSET_FULL, READSET_U);

   @SubObjects( { "", "home-address" })
   public static Readset<User> READSET_FULL_WITH_HOME_ADDRESS_FULL = new Readset<User>(READSET_FULL, UserAddressEntity.READSET_FULL);

   @SubObjects( { "", "office-address" })
   public static Readset<User> READSET_FULL_WITH_OFFICE_ADDRESS_FULL = new Readset<User>(READSET_FULL,
         UserAddressEntity.READSET_FULL);

   @SubObjects( { "", "home-address", "office-address" })
   public static Readset<User> READSET_FULL_WITH_HOME_OFFICE_ADDRESS_FULL = new Readset<User>(READSET_FULL,
         UserAddressEntity.READSET_FULL, UserAddressEntity.READSET_FULL);

   @SubObjects( { "", "home-address", "office-address", "billing-address" })
   public static Readset<User> READSET_FULL_WITH_ALL_ADDRESSES_FULL = new Readset<User>(READSET_FULL,
         UserAddressEntity.READSET_FULL, UserAddressEntity.READSET_FULL, UserAddressEntity.READSET_FULL);

   public static Updateset<User> UPDATESET_FULL = new Updateset<User>(USER_NAME, LAST_MODIFIED_DATE);
   
   public static Updateset<User> UPDATESET_PASS = new Updateset<User>(ENCRYPTED_PASSWORD);

   public static QueryDef FIND_BY_PK = new QueryDef("find-by-pk", UserEntity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id}");

   public static QueryDef FIND_WITH_SUBOBJECTS_BY_PK = new QueryDef("find-with-subobjects-by-pk", UserEntity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLES/> WHERE <JOINS/> AND <FIELD name='user-id'/> = ${key-user-id}");

   public static QueryDef FIND_ALL_BY_USER_ID_IN = new QueryDef("find-all-by-user-id", UserEntity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='user-id'/> IN <IN>${ids}</IN>");

   public static QueryDef FIND_ALL_BY_USER_NAME = new QueryDef("find-all-by-user-name", UserEntity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='user-name'/> = ${user-name}");

   public static final QueryDef INSERT = new QueryDef("insert", UserEntity.class, QueryType.INSERT,
         "INSERT INTO <TABLE/> (<FIELDS/>) VALUES (<VALUES/>)");

   public static final QueryDef UPDATE_BY_PK = new QueryDef("update-by-pk", UserEntity.class, QueryType.UPDATE,
         "UPDATE <TABLE/> SET <FIELDS/> WHERE <FIELD name='user-id'/> = ${key-user-id}");

   public static final QueryDef DELETE_BY_PK = new QueryDef("delete-by-pk", UserEntity.class, QueryType.DELETE,
         "DELETE FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id}");

   public static final QueryDef DELETE_BY_USERNAME = new QueryDef("delete-by-username", UserEntity.class, QueryType.DELETE,
         "DELETE FROM <TABLE/> WHERE <FIELD name='user-name'/> = ${user-name}");

   public static final QueryDef FIND_ALL_USERS = new QueryDef("find-all-users", UserEntity.class, QueryType.SELECT,
         "{ CALL all_users(#{page-size}) }", true);
}
