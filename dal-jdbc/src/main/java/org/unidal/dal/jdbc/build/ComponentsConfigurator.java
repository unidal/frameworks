package org.unidal.dal.jdbc.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.datasource.DefaultDataSourceManager;
import org.unidal.dal.jdbc.datasource.JdbcDataSource;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManager;
import org.unidal.dal.jdbc.engine.DefaultQueryContext;
import org.unidal.dal.jdbc.engine.DefaultQueryEngine;
import org.unidal.dal.jdbc.entity.DefaultDataObjectAccessor;
import org.unidal.dal.jdbc.entity.DefaultDataObjectAssembly;
import org.unidal.dal.jdbc.entity.DefaultDataObjectNaming;
import org.unidal.dal.jdbc.entity.DefaultEntityInfoManager;
import org.unidal.dal.jdbc.mapping.DefaultTableProviderManager;
import org.unidal.dal.jdbc.mapping.RawTableProvider;
import org.unidal.dal.jdbc.query.DefaultQueryExecutor;
import org.unidal.dal.jdbc.query.mysql.MysqlQueryNaming;
import org.unidal.dal.jdbc.query.mysql.MysqlQueryResolver;
import org.unidal.dal.jdbc.query.mysql.MysqlReadHandler;
import org.unidal.dal.jdbc.query.mysql.MysqlWriteHandler;
import org.unidal.dal.jdbc.query.token.DefaultTokenParser;
import org.unidal.dal.jdbc.query.token.resolver.ExpressionResolver;
import org.unidal.dal.jdbc.query.token.resolver.FieldTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.FieldsTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.IfTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.InTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.JoinsTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.ParameterTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.StringTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.TableTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.TablesTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.ValueTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.ValuesTokenResolver;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.test.DatabaseDumper;
import org.unidal.dal.jdbc.test.QueryResultBuilder;
import org.unidal.dal.jdbc.test.TableLoader;
import org.unidal.dal.jdbc.test.TableMaker;
import org.unidal.dal.jdbc.test.TableSchemaBuilder;
import org.unidal.dal.jdbc.transaction.DefaultTransactionManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public final class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(MysqlQueryNaming.class));
      all.add(A(MysqlQueryResolver.class));
      all.add(A(MysqlReadHandler.class));
      all.add(A(MysqlWriteHandler.class));

      all.add(A(DefaultQueryEngine.class));
      all.add(A(DefaultQueryContext.class));
      all.add(A(DefaultEntityInfoManager.class));
      all.add(A(DefaultDataObjectAccessor.class));
      all.add(A(DefaultDataObjectAssembly.class));
      all.add(A(DefaultDataObjectNaming.class));
      all.add(A(DefaultTableProviderManager.class));
      all.add(A(DefaultQueryExecutor.class));
      all.add(A(DefaultTransactionManager.class));
      all.add(A(DefaultTokenParser.class));
      all.add(A(ExpressionResolver.class));

      all.add(A(JdbcDataSource.class));
      all.add(A(DefaultDataSourceManager.class));
      all.add(A(JdbcDataSourceDescriptorManager.class));

      all.addAll(defineTokenResolverComponents());

      all.add(A(RawTableProvider.class));
      all.add(A(RawDao.class));

      // for DAL test
      all.add(A(TableSchemaBuilder.class));
      all.add(A(QueryResultBuilder.class));
      all.add(A(DatabaseDumper.class));
      all.add(A(TableMaker.class));
      all.add(A(TableLoader.class));

      return all;
   }

   private List<Component> defineTokenResolverComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(StringTokenResolver.class));
      all.add(A(ParameterTokenResolver.class));
      all.add(A(FieldTokenResolver.class));
      all.add(A(FieldsTokenResolver.class));
      all.add(A(TableTokenResolver.class));
      all.add(A(TablesTokenResolver.class));
      all.add(A(ValuesTokenResolver.class));
      all.add(A(JoinsTokenResolver.class));
      all.add(A(InTokenResolver.class));
      all.add(A(IfTokenResolver.class));
      all.add(A(ValueTokenResolver.class));

      return all;
   }
}
