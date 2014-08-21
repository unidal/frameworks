package org.unidal.build;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unidal.formatter.DateFormatter;
import org.unidal.formatter.Formatter;
import org.unidal.helper.Threads.LoggerThreadListener;
import org.unidal.helper.Threads.ThreadListener;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.DefaultModuleInitializer;
import org.unidal.initialization.DefaultModuleManager;
import org.unidal.initialization.ModuleContext;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.initialization.ModuleManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(C(ModuleManager.class, DefaultModuleManager.class));
      all.add(C(ModuleInitializer.class, DefaultModuleInitializer.class) //
            .req(ModuleManager.class));
      all.add(C(ModuleContext.class, DefaultModuleContext.class));

      all.add(C(Formatter.class, Date.class.getName(), DateFormatter.class));

      all.add(C(ThreadListener.class, "logger", LoggerThreadListener.class));
      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }
}
