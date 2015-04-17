package org.unidal.dal.jdbc.configuration;

import java.util.List;

import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.JdbcDataSource;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManager;
import org.unidal.dal.jdbc.mapping.SimpleTableProvider;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public abstract class AbstractJdbcResourceConfigurator extends AbstractResourceConfigurator {
	protected void defineDaoComponents(List<Component> all, Class<?>[] daoClasses) {
		for (Class<?> daoClass : daoClasses) {
			all.add(C(daoClass).req(QueryEngine.class));
		}
	}

	protected Component defineJdbcDataSourceComponent(String dataSource, String driver, String url, String user,
	      String password, String connectionProperties) {
		return C(DataSource.class, dataSource, JdbcDataSource.class).req(JdbcDataSourceDescriptorManager.class)
		      .config(
		            E("id").value(dataSource),
		            E("maximum-pool-size").value("3"),
		            E("connection-timeout").value("1s"),
		            E("idle-timeout").value("10m"),
		            E("statement-cache-size").value("1000"),
		            E("properties").add(E("driver").value(driver), E("url").value(url), E("user").value(user),
		                  E("password").value(password), E("connectionProperties").value(connectionProperties)));
	}

	protected Component defineJdbcDataSourceConfigurationManagerComponent(String datasourceFile) {
		return C(JdbcDataSourceDescriptorManager.class).config(E("datasourceFile").value(datasourceFile));
	}

	protected Component defineSimpleTableProviderComponent(String dataSource, String logicalTableName) {
		String physicalTableName = logicalTableName.replace('-', '_');

		return defineSimpleTableProviderComponent(dataSource, logicalTableName, physicalTableName);
	}

	protected Component defineSimpleTableProviderComponent(String dataSource, String logicalTableName,
	      String physicalTableName) {
		return C(TableProvider.class, logicalTableName, SimpleTableProvider.class).config(
		      E("physical-table-name").value(physicalTableName),
		      E("data-source-name").value(dataSource));
	}

	protected void defineSimpleTableProviderComponents(List<Component> all, String dataSource, Class<?>[] entitiClasses) {
		for (Class<?> entityClass : entitiClasses) {
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
