package org.unidal.dal.jdbc.test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.dal.jdbc.test.data.transform.DefaultSaxParser;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.MethodFilter;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.logging.Logger;

public class JdbcTestHelper extends ContainerHolder {
	public void createTables(String dataSource, String group) throws Exception {
		String resource = String.format("/META-INF/dal/jdbc/%s-codegen.xml", group);
		InputStream in = getClass().getResourceAsStream(resource);

		if (in == null) {
			throw new IllegalArgumentException(String.format("Resource(%s) not found!", resource));
		}

		TableMaker maker = lookup(TableMaker.class);

		maker.make(dataSource, in);
	}

	public void defineFunctions(String dataSource, Class<?> functionClass) throws DalException {
		List<Method> methods = Reflects.forMethod().getMethods(functionClass, MethodFilter.PUBLIC_STATIC);

		for (Method method : methods) {
			if (method.getReturnType() == Void.TYPE) {
				getLogger().warn(String.format("Method(%s) return void, IGNORED!", method));
				continue;
			}

			String name = method.getName();
			String className = functionClass.getName();

			executeUpdate(dataSource,
			      String.format("CREATE ALIAS IF NOT EXISTS %s FOR \"%s.%s\"", name.toUpperCase(), className, name));
		}
	}

	public void dumpDeltaTo(String dataSource, String dataXml, String deltaXml, String... tables) throws Exception {
		if (tables.length > 0) {
			DatabaseDumper dumper = lookup(DatabaseDumper.class);
			DatabaseModel model;

			if (dataXml != null) {
				File base = getTestResourceFile(dataXml);

				if (base.exists()) {
					String xml = Files.forIO().readFrom(base, "utf-8");
					DatabaseModel baseModel = DefaultSaxParser.parse(xml);

					model = dumper.dump(baseModel, dataSource, tables);
				} else {
					throw new IllegalStateException(String.format("Resource(%s) is not found!", base.getCanonicalPath()));
				}
			} else {
				model = dumper.dump(null, dataSource, tables);
			}

			File target = getTestResourceFile(deltaXml);
			Files.forIO().writeTo(target, model.toString());
		}
	}

	public void dumpTo(String dataSource, String dataXml, String... tables) throws Exception {
		dumpDeltaTo(dataSource, null, dataXml, tables);
	}

	public List<RawDataObject> executeQuery(String dataSource, String sql) throws DalException {
		RawDao dao = lookup(RawDao.class);

		return dao.executeQuery(dataSource, sql);
	}

	public void executeUpdate(String dataSource, String sql) throws DalException {
		RawDao dao = lookup(RawDao.class);

		dao.executeUpdate(dataSource, sql);
	}

	public Logger getLogger() {
		return getContainer().getLogger();
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

	public void loadFrom(String dataSource, String dataXml) throws Exception {
		InputStream in = getClass().getResourceAsStream(dataXml);

		if (in == null) {
			throw new IllegalArgumentException(String.format("Resource(%s) not found!", dataXml));
		}

		TableLoader loader = lookup(TableLoader.class);

		loader.loadFrom(dataSource, in);
		release(loader);
	}

	public void setUp() throws Exception {
		System.setProperty("devMode", "true");

		// super.setUp();

		// defineComponent(DataSourceManager.class, TestDataSourceManager.class) //
		// .req(JdbcDataSourceDescriptorManager.class);
		// defineFunctions(StringFunction.class);
	}

	public void showQuery(String dataSource, String sql) throws DalException {
		long start = System.currentTimeMillis();
		List<RawDataObject> rowset = executeQuery(dataSource, sql);
		long end = System.currentTimeMillis();

		System.out.println(new QueryResultBuilder().build(rowset));
		System.out.println(String.format("%s rows in set (%.3f sec)", rowset.size(), (end - start) / 1000.0));
		System.out.println();
	}

	public void tearDown(String dataSource) throws Exception {
		executeUpdate(dataSource, "SHUTDOWN");
		// super.tearDown();
	}
}
