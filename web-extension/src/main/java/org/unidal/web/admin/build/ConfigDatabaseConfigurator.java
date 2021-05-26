package org.unidal.web.admin.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

final class ConfigDatabaseConfigurator extends AbstractJdbcResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();


      defineSimpleTableProviderComponents(all, "config", org.unidal.web.admin.dal.config._INDEX.getEntityClasses());
      defineDaoComponents(all, org.unidal.web.admin.dal.config._INDEX.getDaoClasses());

      return all;
   }
}
