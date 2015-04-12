package org.unidal.dal.jdbc.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

/**
 * <xmp>
 * 
 * <dependency>
 * <groupId>com.h2database</groupId>
 * <artifactId>h2</artifactId>
 * <version>1.4.186</version>
 * <scope>test</scope>
 * </dependency>
 * 
 * </xmp>
 */
public abstract class JdbcTestCase extends ComponentTestCase {
   protected void createTables(String name) throws Exception {
      String resource = String.format("/META-INF/dal/jdbc/%s-codegen.xml", name);
      InputStream in = getClass().getResourceAsStream(resource);

      if (in == null) {
         throw new IllegalArgumentException(String.format("Resource(%s) not found!", resource));
      }

      TableMaker maker = lookup(TableMaker.class);

      maker.make(getDefaultDataSource(), in);
   }

   protected void dumpTo(File dataFile, String table) throws DalException, IOException {
      DatabaseDumper dumper = lookup(DatabaseDumper.class);
      DatabaseModel model = dumper.dump(getDefaultDataSource(), table);

      Files.forIO().writeTo(dataFile, model.toString());
   }

   protected List<RawDataObject> executeQuery(String sql) throws DalException {
      RawDao dao = lookup(RawDao.class);

      return dao.executeQuery(getDefaultDataSource(), sql);
   }

   protected void executeUpdate(String sql) throws DalException {
      RawDao dao = lookup(RawDao.class);

      dao.executeUpdate(getDefaultDataSource(), sql);
   }

   protected abstract String getDefaultDataSource();

   protected void loadFrom(String dataXml) throws Exception {
      InputStream in = getClass().getResourceAsStream(dataXml);

      if (in == null) {
         throw new IllegalArgumentException(String.format("Resource(%s) not found!", dataXml));
      }

      TableLoader loader = lookup(TableLoader.class);

      loader.loadFrom(getDefaultDataSource(), in);
      release(loader);
   }

   @Before
   @Override
   public void setUp() throws Exception {
      super.setUp();

      defineComponent(DataSourceManager.class, TestDataSourceManager.class);
   }

   protected void showQuery(String sql) throws DalException {
      long start = System.currentTimeMillis();
      List<RawDataObject> rowset = executeQuery(sql);
      long end = System.currentTimeMillis();

      System.out.println(new QueryResultBuilder().build(rowset));
      System.out.println(String.format("%s rows in set (%.3f sec)", rowset.size(), (end - start) / 1000.0));
      System.out.println();
   }
}
