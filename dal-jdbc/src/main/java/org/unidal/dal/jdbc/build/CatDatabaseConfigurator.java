package org.unidal.dal.jdbc.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

final class CatDatabaseConfigurator extends AbstractJdbcResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(defineJdbcDataSourceComponent("cat", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/cat", "cat", "cat", "<![CDATA[useUnicode=true&characterEncoding=UTF-8&autoReconnect=true]]>"));

      defineSimpleTableProviderComponents(all, "cat", org.unidal.dal.jdbc.cat.metrics._INDEX.getEntityClasses());
      defineDaoComponents(all, org.unidal.dal.jdbc.cat.metrics._INDEX.getDaoClasses());

      return all;
   }
}
