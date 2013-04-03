package org.unidal.dal.jdbc.entity;

public interface EntityInfoManager {
   public EntityInfo getEntityInfo(Class<?> entityClass);

   public EntityInfo getEntityInfo(String logicalName);

   public String getQuotedName(String name);

   public void register(Class<?> entityClass);

}