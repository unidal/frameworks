package org.unidal.web.mvc.model;

import java.util.List;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.web.mvc.Module;

@Named
public class ModuleRegistry extends ContainerHolder implements Initializable {
   private String m_defaultModuleName;

   private Module m_defaultModule;

   private List<Module> m_modules;

   public Module getDefaultModule() {
      return m_defaultModule;
   }

   public List<Module> getModules() {
      return m_modules;
   }

   @Override
   public void initialize() throws InitializationException {
      if (m_defaultModuleName != null) {
         m_defaultModule = lookup(Module.class, m_defaultModuleName);
      }

      m_modules = lookupList(Module.class);
   }

   public void setDefaultModule(String defaultModuleName) {
      m_defaultModuleName = defaultModuleName;
   }
}
