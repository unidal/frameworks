package org.unidal.dal.jdbc.test;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public class UserTestConfigurator extends AbstractJdbcResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new UserTestConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      defineSimpleTableProviderComponents(all, "user", org.unidal.dal.jdbc.user._INDEX.getEntityClasses());
      defineDaoComponents(all, org.unidal.dal.jdbc.user._INDEX.getDaoClasses());

      return all;
   }

   @Override
   protected Class<?> getTestClass() {
      return UserTest.class;
   }
}
