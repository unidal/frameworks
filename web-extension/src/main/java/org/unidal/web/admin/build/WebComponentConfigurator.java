package org.unidal.web.admin.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.mvc.model.ModuleRegistry;

class WebComponentConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(ModuleRegistry.class).config(E("default-module").value(org.unidal.web.admin.user.UserModule.class.getName())));
  
      all.add(A(org.unidal.web.admin.user.UserModule.class));

      all.add(A(org.unidal.web.admin.user.login.Handler.class));
      all.add(A(org.unidal.web.admin.user.login.JspViewer.class));

      all.add(A(org.unidal.web.admin.config.ConfigModule.class));

      all.add(A(org.unidal.web.admin.config.home.Handler.class));
      all.add(A(org.unidal.web.admin.config.home.JspViewer.class));

      all.add(A(org.unidal.web.admin.config.refresh.Handler.class));
      all.add(A(org.unidal.web.admin.config.refresh.JspViewer.class));

      return all;
   }
}
