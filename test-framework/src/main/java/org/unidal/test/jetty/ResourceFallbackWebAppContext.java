package org.unidal.test.jetty;

import java.io.File;
import java.net.URL;

import org.eclipse.jetty.webapp.WebAppContext;

public class ResourceFallbackWebAppContext extends WebAppContext {
   private WebModuleManager m_manager;

   public ResourceFallbackWebAppContext() throws Exception {
      WebModuleManager manager = new WebModuleManager();

      super.setErrorHandler(new ResourceFallbackErrorHandler(manager));

      m_manager = manager;
   }

   @Override
   public String getDescriptor() {
      String webxml = super.getDescriptor();

      if (webxml != null && !new File(webxml).exists()) {
         // not exist
         URL url = m_manager.getResourceUrl("/WEB-INF/web.xml");

         if (url != null) {
            return url.toExternalForm();
         }
      }

      return webxml;
   }
}
