package org.unidal.test.user.address.dal;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.annotation.Variable;

@Entity(logicalName = "user-address", alias = "ua")
public class UserAddressEntity {

   @Attribute(field = "user_id", nullable = false, primaryKey = true)
   public static DataField USER_ID = new DataField("user-id");

   @Attribute(field = "type", nullable = false, primaryKey = true)
   public static DataField TYPE = new DataField("type");

   @Attribute(field = "address", nullable = false)
   public static DataField ADDRESS = new DataField("address");

   @Variable
   public static DataField KEY_USER_ID = new DataField("key-user-id");

   @Variable
   public static DataField KEY_TYPE = new DataField("key-type");

   public static Readset<UserAddress> READSET_FULL = new Readset<UserAddress>(USER_ID, TYPE, ADDRESS);

   public static Updateset<UserAddress> UPDATESET_FULL = new Updateset<UserAddress>(TYPE, ADDRESS);

   public static QueryDef FIND_BY_PK = new QueryDef("find-by-pk", UserAddressEntity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id} AND <FIELD name='type'/> = ${key-type}");

   public static final QueryDef INSERT = new QueryDef("insert", UserAddressEntity.class, QueryType.INSERT,
         "INSERT INTO <TABLE/> (<FIELDS/>) VALUES (<VALUES/>)");

   public static final QueryDef UPDATE_BY_PK = new QueryDef("update-by-pk", UserAddressEntity.class, QueryType.UPDATE,
         "UPDATE <TABLE/> SET <FIELDS/> WHERE <FIELD name='user-id'/> = ${key-user-id} AND <FIELD name='type'/> = ${key-type}");

   public static final QueryDef DELETE_BY_PK = new QueryDef("delete-by-pk", UserAddressEntity.class, QueryType.DELETE,
         "DELETE FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id} AND <FIELD name='type'/> = ${key-type}");

   public static final QueryDef DELETE_ALL_BY_USER_ID = new QueryDef("delete-all-by-user-id", UserAddressEntity.class, QueryType.DELETE,
         "DELETE FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id}");

}
