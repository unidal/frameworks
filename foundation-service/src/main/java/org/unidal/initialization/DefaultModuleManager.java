package org.unidal.initialization;

import java.util.List;

import org.unidal.helper.Splitters;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;

@Named(type = ModuleManager.class)
public class DefaultModuleManager extends ContainerHolder implements ModuleManager {
   @InjectAttribute
   private String m_topLevelModules;

   @Override
   public Module[] getTopLevelModules() {
      if (m_topLevelModules != null && m_topLevelModules.length() > 0) {
         List<String> hints = Splitters.by(',').trim().noEmptyItem().split(m_topLevelModules);
         Module[] topLevelModules = new Module[hints.size()];
         int index = 0;

         for (String hint : hints) {
            topLevelModules[index++] = lookup(Module.class, hint);
         }

         return topLevelModules;
      } else {
         return new Module[0];
      }
   }

   public void setTopLevelModules(String topLevelModules) {
      m_topLevelModules = topLevelModules;
   }
}
