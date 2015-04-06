package org.unidal.dal.jdbc.entity;

public interface EntityInfoManager {
   public EntityInfo getEntityInfo(Class<?> entityClass);

   public EntityInfo getEntityInfo(String logicalName);

   public void register(Class<?> entityClass);

}