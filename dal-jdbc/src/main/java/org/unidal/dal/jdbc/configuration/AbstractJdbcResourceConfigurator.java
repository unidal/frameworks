package org.unidal.dal.jdbc.configuration;

import java.util.List;

import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.datasource.DefaultDataSourceProvider;
import org.unidal.dal.jdbc.mapping.SimpleTableProvider;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public abstract class AbstractJdbcResourceConfigurator extends AbstractResourceConfigurator {
   protected void defineDaoComponents(List<Component> all, Class<?>[] daoClasses) {
      for (Class<?> daoClass : daoClasses) {
         if (daoClass.getAnnotation(Named.class) != null) {
            all.add(A(daoClass));
         } else {
            all.add(C(daoClass).req(QueryEngine.class));
         }
      }
   }

   protected Component defineJdbcDataSourceConfigurationManagerComponent(String datasourceFile) {
      return A(DefaultDataSourceProvider.class) //
            .config(E("datasourceFile").value(datasourceFile));
   }

   protected Component defineSimpleTableProviderComponent(String dataSource, String logicalTableName) {
      String physicalTableName = logicalTableName.replace('-', '_');

      return defineSimpleTableProviderComponent(dataSource, logicalTableName, physicalTableName);
   }

   protected Component defineSimpleTableProviderComponent(String dataSource, String logicalTableName, String physicalTableName) {
      return A(SimpleTableProvider.class, logicalTableName) //
            .config(E("physical-table-name").value(physicalTableName), E("data-source-name").value(dataSource));
   }

   protected void defineSimpleTableProviderComponents(List<Component> all, String dataSource, Class<?>[] entityClasses) {
      for (Class<?> entityClass : entityClasses) {
         Entity entity = entityClass.getAnnotation(Entity.class);
         String logicalName = entity.logicalName();
         String physicalName = entity.physicalName();

         if (physicalName.length() == 0) {
            logicalName.replace('-', '_');
         }

         all.add(defineSimpleTableProviderComponent(dataSource, logicalName, physicalName));
      }
   }
}
