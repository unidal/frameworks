package org.unidal.test.jetty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;
import org.mortbay.resource.URLResource;

public class ResourceFallbackWebAppContext extends WebAppContext {
   private WebModuleResourceManager m_manager;

   public ResourceFallbackWebAppContext() throws Exception {
      WebModuleResourceManager manager = new WebModuleResourceManager();

      super.setErrorHandler(new ResourceFallbackErrorHandler(manager));
      m_manager = manager;
   }

   @Override
   public Resource getResource(String uriInContext) throws MalformedURLException {
      Resource resource = super.getResource(uriInContext);

      if (!resource.exists()) {
         URL url = m_manager.getResource(uriInContext);

         if (url != null) {
            try {
               return new URLResource(url, url.openConnection()) {
                  private static final long serialVersionUID = 1L;
               };
            } catch (Exception e) {
               // ignore it
            }
         }
      }

      return resource;
   }

   @Override
   public String getDescriptor() {
      String webxml = super.getDescriptor();

      if (webxml != null && !new File(webxml).exists()) {
         // not exist
         URL url = m_manager.getResource("/WEB-INF/web.xml");

         if (url != null) {
            return url.toExternalForm();
         }
      }

      return webxml;
   }
}
