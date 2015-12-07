package org.unidal.dal.jdbc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.mapping.SimpleTableProvider;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.configuration.Component;
import org.unidal.test.user.address.dal.UserAddressDao;
import org.unidal.test.user.dal.UserDao;

public class PlexusConfigurator extends AbstractJdbcResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new PlexusConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();
      String resource = DataSource.class.getPackage().getName().replace('.', '/') + "/datasource.xml";

      all.add(defineJdbcDataSourceConfigurationManagerComponent(resource));

      all.add(C(TableProvider.class, "user", SimpleTableProvider.class).config(E("data-source-name").value("jdbc-dal"),
            E("physical-table-name").value("user")));

      all.add(C(TableProvider.class, "user2", SimpleTableProvider.class).config(
            E("data-source-name").value("jdbc-dal"), E("physical-table-name").value("user2")));

      all.add(C(TableProvider.class, "user-address", SimpleTableProvider.class).config(
            E("data-source-name").value("jdbc-dal"), E("physical-table-name").value("user_address")));

      all.add(C(UserDao.class).req(QueryEngine.class));
      all.add(C(UserAddressDao.class).req(QueryEngine.class));

      return all;
   }

   @Override
   protected File getConfigurationFile() {
      return new File("src/test/resources/META-INF/plexus/components.xml");
   }
}
