package org.unidal.dal.jdbc.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.datasource.DefaultDataSourceManager;
import org.unidal.dal.jdbc.datasource.JdbcDataSource;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManager;
import org.unidal.dal.jdbc.engine.DefaultQueryContext;
import org.unidal.dal.jdbc.engine.DefaultQueryEngine;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.entity.DataObjectAssembly;
import org.unidal.dal.jdbc.entity.DataObjectNaming;
import org.unidal.dal.jdbc.entity.DefaultDataObjectAccessor;
import org.unidal.dal.jdbc.entity.DefaultDataObjectAssembly;
import org.unidal.dal.jdbc.entity.DefaultDataObjectNaming;
import org.unidal.dal.jdbc.entity.DefaultEntityInfoManager;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.mapping.DefaultTableProviderManager;
import org.unidal.dal.jdbc.mapping.RawTableProvider;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.dal.jdbc.mapping.TableProviderManager;
import org.unidal.dal.jdbc.msyql.MysqlQueryResolver;
import org.unidal.dal.jdbc.msyql.MysqlReservedKeyword;
import org.unidal.dal.jdbc.query.DefaultQueryExecutor;
import org.unidal.dal.jdbc.query.QueryExecutor;
import org.unidal.dal.jdbc.query.QueryResolver;
import org.unidal.dal.jdbc.query.ReservedKeyword;
import org.unidal.dal.jdbc.query.token.DefaultTokenParser;
import org.unidal.dal.jdbc.query.token.TokenParser;
import org.unidal.dal.jdbc.query.token.TokenType;
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
import org.unidal.dal.jdbc.query.token.resolver.TokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.ValueTokenResolver;
import org.unidal.dal.jdbc.query.token.resolver.ValuesTokenResolver;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.transaction.DefaultTransactionManager;
import org.unidal.dal.jdbc.transaction.TransactionManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.message.MessageProducer;

public final class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(C(ReservedKeyword.class, MysqlReservedKeyword.class));
      all.add(C(QueryResolver.class, MysqlQueryResolver.class) //
            .req(TokenParser.class));

      all.add(C(QueryEngine.class, DefaultQueryEngine.class) //
            .req(EntityInfoManager.class, QueryExecutor.class, TransactionManager.class) //
            .req(QueryResolver.class));
      all.add(C(QueryContext.class, DefaultQueryContext.class) //
            .is(PER_LOOKUP));
      all.add(C(EntityInfoManager.class, DefaultEntityInfoManager.class) //
            .req(ReservedKeyword.class));
      all.add(C(DataObjectAccessor.class, DefaultDataObjectAccessor.class) //
            .req(DataObjectNaming.class));
      all.add(C(DataObjectAssembly.class, DefaultDataObjectAssembly.class) //
            .req(DataObjectAccessor.class, DataObjectNaming.class));
      all.add(C(DataObjectNaming.class, DefaultDataObjectNaming.class));
      all.add(C(TableProviderManager.class, DefaultTableProviderManager.class));

      all.add(C(QueryExecutor.class, DefaultQueryExecutor.class) //
            .req(TransactionManager.class, DataObjectAccessor.class, DataObjectAssembly.class) //
            .req(DataSourceManager.class, MessageProducer.class));
      all.add(C(TransactionManager.class, DefaultTransactionManager.class) //
            .req(TableProviderManager.class, DataSourceManager.class));

      all.add(C(TokenParser.class, DefaultTokenParser.class));
      all.add(C(ExpressionResolver.class) //
            .req(TokenParser.class));

      defineDataSourceComponents(all);
      defineTokenResolverComponents(all);

      all.add(C(TableProvider.class, "raw", RawTableProvider.class) //
            .config(E("logical-table-name").value("raw")));
      all.add(C(RawDao.class).req(QueryEngine.class));

      return all;
   }

   private void defineDataSourceComponents(List<Component> all) {
      all.add(C(DataSource.class, "jdbc", JdbcDataSource.class).is(PER_LOOKUP));
      all.add(C(DataSourceManager.class, DefaultDataSourceManager.class) //
            .req(JdbcDataSourceDescriptorManager.class));
   }

   private void defineTokenResolverComponents(List<Component> all) {
      all.add(C(TokenResolver.class, TokenType.STRING, StringTokenResolver.class));
      all.add(C(TokenResolver.class, TokenType.PARAM, ParameterTokenResolver.class) //
            .req(DataObjectAccessor.class));
      all.add(C(TokenResolver.class, TokenType.FIELD, FieldTokenResolver.class) //
            .req(EntityInfoManager.class, ExpressionResolver.class));
      all.add(C(TokenResolver.class, TokenType.FIELDS, FieldsTokenResolver.class) //
            .req(EntityInfoManager.class, ExpressionResolver.class));
      all.add(C(TokenResolver.class, TokenType.TABLE, TableTokenResolver.class) //
            .req(TableProviderManager.class));
      all.add(C(TokenResolver.class, TokenType.TABLES, TablesTokenResolver.class) //
            .req(TableProviderManager.class));
      all.add(C(TokenResolver.class, TokenType.VALUES, ValuesTokenResolver.class) //
            .req(ExpressionResolver.class));
      all.add(C(TokenResolver.class, TokenType.JOINS, JoinsTokenResolver.class));
      all.add(C(TokenResolver.class, TokenType.IN, InTokenResolver.class));
      all.add(C(TokenResolver.class, TokenType.IF, IfTokenResolver.class) //
            .req(DataObjectAccessor.class));
      all.add(C(TokenResolver.class, TokenType.VALUE, ValueTokenResolver.class) //
            .req(ExpressionResolver.class));
   }
}
