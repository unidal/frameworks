package org.unidal.dal.jdbc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.dal.jdbc.datasource.DataSource;
import org.unidal.dal.jdbc.mapping.SimpleTableProvider;
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

      all.add(A(SimpleTableProvider.class, "user") //
            .config(E("data-source-name").value("jdbc-dal"), E("physical-table-name").value("user")));

      all.add(A(SimpleTableProvider.class, "user2") //
            .config(E("data-source-name").value("jdbc-dal"), E("physical-table-name").value("user2")));

      all.add(A(SimpleTableProvider.class, "user-address") //
            .config(E("data-source-name").value("jdbc-dal"), E("physical-table-name").value("user_address")));

      all.add(A(UserDao.class));
      all.add(A(UserAddressDao.class));

      return all;
   }

   @Override
   protected File getConfigurationFile() {
      return new File("src/test/resources/META-INF/plexus/components.xml");
   }
}
