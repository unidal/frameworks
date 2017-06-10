package org.unidal.dal.jdbc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.dal.jdbc.datasource.DataSourceTest;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManagerTest;
import org.unidal.dal.jdbc.entity.EntityManagerTest;
import org.unidal.dal.jdbc.intg.MultipleTablesTest;
import org.unidal.dal.jdbc.intg.SingleTableTest;
import org.unidal.dal.jdbc.intg.UserDaoTest;
import org.unidal.dal.jdbc.mapping.TableProviderTest;
import org.unidal.dal.jdbc.query.QueryResolverTest;
import org.unidal.dal.jdbc.query.token.TokenParserTest;
import org.unidal.dal.jdbc.query.token.resolver.FieldTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.FieldsTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.IfTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.InTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.JoinsTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.ParameterTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.StringTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.TableTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.TablesTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.ValueTokenResolverTest;
import org.unidal.dal.jdbc.query.token.resolver.ValuesTokenResolverTest;
import org.unidal.dal.jdbc.test.UserTest;
import org.unidal.dal.jdbc.transaction.TransactionManagerTest;

@RunWith(Suite.class)
@SuiteClasses({

SingleTableTest.class,

MultipleTablesTest.class,

UserDaoTest.class,

DataSourceTest.class,

JdbcDataSourceDescriptorManagerTest.class,

EntityManagerTest.class,

TableProviderTest.class,

QueryResolverTest.class,

TokenParserTest.class,

FieldsTokenResolverTest.class,

FieldTokenResolverTest.class,

IfTokenResolverTest.class,

InTokenResolverTest.class,

JoinsTokenResolverTest.class,

ParameterTokenResolverTest.class,

StringTokenResolverTest.class,

TablesTokenResolverTest.class,

TableTokenResolverTest.class,

ValuesTokenResolverTest.class,

ValueTokenResolverTest.class,

TransactionManagerTest.class,

UserTest.class,

})
public class AllTests {

}
