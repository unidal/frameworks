package org.unidal.web.admin.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.authorization.MyAccessControl;
import org.unidal.web.authorization.MyApplication;
import org.unidal.web.authorization.MyAuthorization;
import org.unidal.web.authorization.MyPageValidator;
import org.unidal.web.authorization.MyRealm;
import org.unidal.web.authorization.MyUser;
import org.unidal.web.config.DefaultConfigService;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(MyAccessControl.class));
      all.add(A(MyRealm.class));
      all.add(A(MyAuthorization.class));
      all.add(A(MyApplication.class));
      all.add(A(MyUser.class));

      all.add(A(MyPageValidator.class));

      all.add(A(DefaultConfigService.class));

      all.addAll(new ConfigDatabaseConfigurator().defineComponents());
      all.addAll(new WebComponentConfigurator().defineComponents());

      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }
}
