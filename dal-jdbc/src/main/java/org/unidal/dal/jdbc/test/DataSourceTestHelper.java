package org.unidal.dal.jdbc.test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.datasource.DataSourceManager;
import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManager;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.dal.jdbc.test.data.transform.DefaultSaxParser;
import org.unidal.dal.jdbc.test.function.StringFunction;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.MethodFilter;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.RequirementModel;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

public class DataSourceTestHelper extends ContainerHolder implements LogEnabled {
   private String m_dataSource;

   private Logger m_logger;

   public void createTables(String group) throws Exception {
      String resource = String.format("/META-INF/dal/jdbc/%s-codegen.xml", group);
      InputStream in = getClass().getResourceAsStream(resource);

      if (in == null) {
         throw new IllegalArgumentException(String.format("Resource(%s) not found!", resource));
      }

      TableMaker maker = lookup(TableMaker.class);

      maker.make(getDefaultDataSource(), in);
   }

   public void defineFunctions(Class<?> functionClass) throws DalException {
      List<Method> methods = Reflects.forMethod().getMethods(functionClass, MethodFilter.PUBLIC_STATIC);

      for (Method method : methods) {
         if (method.getReturnType() == Void.TYPE) {
            m_logger.warn(String.format("Method(%s) return void, IGNORED!", method));
            continue;
         }

         String name = method.getName();
         String className = functionClass.getName();

         executeUpdate(String.format("CREATE ALIAS IF NOT EXISTS %s FOR \"%s.%s\"", //
               name.toUpperCase(), className, name));
      }
   }

   public void dumpDeltaTo(String baseXmlFile, String deltaXmlFile, String... tables) throws Exception {
      if (tables.length > 0) {
         DatabaseDumper dumper = lookup(DatabaseDumper.class);
         String ds = getDefaultDataSource();
         DatabaseModel model;

         if (baseXmlFile != null) {
            File base = getTestResourceFile(baseXmlFile);

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

         File target = getTestResourceFile(deltaXmlFile);
         Files.forIO().writeTo(target, model.toString());
      }
   }

   public void dumpTo(String dataXmlFile, String... tables) throws Exception {
      dumpDeltaTo(null, dataXmlFile, tables);
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public List<RawDataObject> executeQuery(String sql) throws DalException {
      RawDao dao = lookup(RawDao.class);

      return dao.executeQuery(getDefaultDataSource(), sql);
   }

   public void executeUpdate(String sql) throws DalException {
      RawDao dao = lookup(RawDao.class);

      dao.executeUpdate(getDefaultDataSource(), sql);
   }

   public String getDefaultDataSource() {
      return m_dataSource;
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

   public void loadFrom(String dataXmlResource) throws Exception {
      InputStream in = getClass().getResourceAsStream(dataXmlResource);

      if (in == null) {
         throw new IllegalArgumentException(String.format("Resource(%s) not found!", dataXmlResource));
      }

      TableLoader loader = lookup(TableLoader.class);

      loader.loadFrom(getDefaultDataSource(), in);
      release(loader);
   }

   public void setDataSource(String dataSource) {
      m_dataSource = dataSource;
   }

   @SuppressWarnings("unchecked")
   public void setUp(PlexusContainer container) throws Exception {
      Class<DataSourceManager> implementation = (Class<DataSourceManager>) (Class<? extends DataSourceManager>) TestDataSourceManager.class;
      ComponentModel model = new ComponentModel();

      model.setRole(DataSourceManager.class.getName());
      model.setImplementation(implementation.getName());

      model.addRequirement(new RequirementModel().setRole(JdbcDataSourceDescriptorManager.class.getName()));
      container.addComponentModel(model);

      // keep it after component hacking
      defineFunctions(StringFunction.class);
   }

   public void showQuery(String sql) throws DalException {
      long start = System.currentTimeMillis();
      List<RawDataObject> rowset = executeQuery(sql);
      long end = System.currentTimeMillis();

      System.out.println(new QueryResultBuilder().build(rowset));
      System.out.println(String.format("%s rows in set (%.3f sec)", rowset.size(), (end - start) / 1000.0));
      System.out.println();
   }

   public void tearDown() throws Exception {
      executeUpdate("SHUTDOWN");
   }
}
