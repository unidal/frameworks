package org.unidal.initialization;

import java.util.LinkedHashSet;
import java.util.Set;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = ModuleInitializer.class)
public class DefaultModuleInitializer implements ModuleInitializer {
   @Inject
   private ModuleManager m_manager;

   private int m_index = 1;

   @Override
   public void execute(ModuleContext ctx) {
      Module[] modules = m_manager.getTopLevelModules();

      execute(ctx, modules);
   }

   @Override
   public void execute(ModuleContext ctx, Module... modules) {
      Set<Module> all = new LinkedHashSet<Module>();

      info(ctx, "Initializing top level modules:");

      for (Module module : modules) {
         info(ctx, "   " + module.getClass().getName());
      }

      try {
         expandAll(ctx, modules, all);

         for (Module module : all) {
            if (!module.isInitialized()) {
               executeModule(ctx, module, m_index++);
            }
         }
      } catch (RuntimeException e) {
         throw e;
      } catch (Error e) {
         throw e;
      } catch (Exception e) {
         throw new RuntimeException("Error when initializing modules! Exception: " + e, e);
      }
   }

   private synchronized void executeModule(ModuleContext ctx, Module module, int index) throws Exception {
      long start = System.currentTimeMillis();

      // set flag to avoid re-entrance
      module.setInitialized(true);

      info(ctx, index + " ------ " + module.getClass().getName() + " ...");

      // execute itself after its dependencies
      module.initialize(ctx);

      long end = System.currentTimeMillis();
      info(ctx, index + " ------ " + module.getClass().getName() + " DONE in " + (end - start) + " ms.");
   }

   private void expandAll(ModuleContext ctx, Module[] modules, Set<Module> all) throws Exception {
      if (modules != null) {
         for (Module module : modules) {
            if (module != null && !all.contains(module)) {
               if (module instanceof AbstractModule) {
                  ((AbstractModule) module).setup(ctx);
               }

               expandAll(ctx, module.getDependencies(ctx), all);

               all.add(module);
            }
         }
      }
   }

   private void info(ModuleContext ctx, String message) {
      // either -DdevMode or -DdevMode=true is okay
      String devMode = System.getProperty("devMode", null);

      if (devMode != null && (devMode.length() == 0 || "true".equals(devMode))) {
         ctx.info(message);
      }
   }
}
