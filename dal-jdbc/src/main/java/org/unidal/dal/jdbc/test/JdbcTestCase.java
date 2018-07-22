package org.unidal.dal.jdbc.test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.dal.jdbc.test.data.transform.DefaultSaxParser;
import org.unidal.dal.jdbc.test.function.StringFunction;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.MethodFilter;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.logging.Logger;

public abstract class JdbcTestCase extends ComponentTestCase {
   protected void createTables(String group) throws Exception {
      String resource = String.format("/META-INF/dal/jdbc/%s-codegen.xml", group);
      InputStream in = getClass().getResourceAsStream(resource);

      if (in == null) {
         throw new IllegalArgumentException(String.format("Resource(%s) not found!", resource));
      }

      TableMaker maker = lookup(TableMaker.class);

      maker.make(getDefaultDataSource(), in);
   }

   protected void defineFunctions(Class<?> functionClass) throws DalException {
      List<Method> methods = Reflects.forMethod().getMethods(functionClass, MethodFilter.PUBLIC_STATIC);

      for (Method method : methods) {
         if (method.getReturnType() == Void.TYPE) {
            getLogger().warn(String.format("Method(%s) return void, IGNORED!", method));
            continue;
         }

         String name = method.getName();
         String className = functionClass.getName();

         executeUpdate(
               String.format("CREATE ALIAS IF NOT EXISTS %s FOR \"%s.%s\"", name.toUpperCase(), className, name));
      }
   }

   protected void dumpDeltaTo(String dataXml, String deltaXml, String... tables) throws Exception {
      if (tables.length > 0) {
         DatabaseDumper dumper = lookup(DatabaseDumper.class);
         String ds = getDefaultDataSource();
         DatabaseModel model;

         if (dataXml != null) {
            File base = getTestResourceFile(dataXml);

            if (base.exists()) {
               String xml = Files.forIO().readFrom(base, "utf-8");
               DatabaseModel baseModel = DefaultSaxParser.parse(xml);

               model = dumper.dump(baseModel, ds, tables);
            } else {
               throw new IllegalStateException(String.format("Resource(%s) is not found!", base.getCanonicalPath()));
            }
         } else {
            model = dumper.dump(null, ds, tables);
         }

         File target = getTestResourceFile(deltaXml);
         Files.forIO().writeTo(target, model.toString());
      }
   }

   private File getTestResourceFile(String deltaXml) {
      File base = new File("src/test/resources");
      File file;

      if (deltaXml.startsWith("/")) {
         file = new File(base, deltaXml);
      } else {
         String packageName = getClass().getPackage().getName();

         file = new File(base, packageName.replace('.', '/') + "/" + deltaXml);
      }

      return file;
   }

   protected void dumpTo(String dataXml, String... tables) throws Exception {
      dumpDeltaTo(null, dataXml, tables);
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

   protected Logger getLogger() {
      return getContainer().getLogger();
   }

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
      System.setProperty("devMode", "true");

      super.setUp();

      define(TestDataSourceManager.class);
      defineFunctions(StringFunction.class);
   }

   protected void showQuery(String sql) throws DalException {
      long start = System.currentTimeMillis();
      List<RawDataObject> rowset = executeQuery(sql);
      long end = System.currentTimeMillis();

      System.out.println(new QueryResultBuilder().build(rowset));
      System.out.println(String.format("%s rows in set (%.3f sec)", rowset.size(), (end - start) / 1000.0));
      System.out.println();
   }

   @Override
   public void tearDown() throws Exception {
      try {
         executeUpdate("SHUTDOWN");
      } catch (Exception e) {
         e.printStackTrace();
      }

      super.tearDown();
   }
}
