package org.unidal.test.user.dal.invalid;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Entity;

@Entity(logicalName = "user2", alias = "u")
public final class User2Entity {

   @Attribute(field = "user_id", nullable = false, primaryKey = true, autoIncrement = true)
   public static DataField USER_ID = new DataField("user-id");

   @Attribute(field = "user_name", nullable = false)
   public static DataField USER_NAME = new DataField("user-name");

   @Attribute(field = "creation_date", insertExpr = "NOW()")
   public static DataField CREATION_DATE = new DataField("creation-date");

   public static Readset<Object> READSET_FULL = new Readset<Object>(USER_ID, USER_NAME);

   public static Updateset<Object> UPDATESET_FULL = new Updateset<Object>(USER_NAME);

   public static QueryDef FIND_BY_PK = new QueryDef("find-by-pk", User2Entity.class, QueryType.SELECT,
         "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='user-id'/> = ${key-user-id}");
}
