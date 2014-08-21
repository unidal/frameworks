package org.unidal.test.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.FileResource;
import org.mortbay.resource.Resource;
import org.unidal.helper.Files;

public class ResourceFallbackWebAppContext extends WebAppContext {
   private WebModuleResourceManager m_manager;

   private File m_baseDir;

   public ResourceFallbackWebAppContext() throws Exception {
      WebModuleResourceManager manager = new WebModuleResourceManager();

      super.setErrorHandler(new ResourceFallbackErrorHandler(manager));

      m_manager = manager;
      m_baseDir = new File(System.getProperty("java.io.tmpdir"), "jsp");
   }

   @Override
   public Resource getResource(String uriInContext) throws MalformedURLException {
      Resource resource = super.getResource(uriInContext);

      if (!resource.exists()) {
         URL url = m_manager.getResource(uriInContext);

         if (url != null) {
            try {
               File file = new File(m_baseDir, uriInContext);

               file.getParentFile().mkdirs();
               Files.forIO().copy(url.openStream(), new FileOutputStream(file));
               return new FileResource(file.toURI().toURL());
            } catch (Exception e) {
               e.printStackTrace();
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
