package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.resource.URLResource;

public class WebModuleResource extends ResourceCollection {
   private ConcurrentMap<String, Resource> m_resources = new ConcurrentHashMap<String, Resource>();

   private WebModuleManager m_manager;

   private File m_docRoot;

   public WebModuleResource(File docRoot) throws Exception {
      super(docRoot.getPath());

      m_docRoot = docRoot;
      m_manager = new WebModuleManager();
   }

   public InputStream getStream(String uri) throws IOException {
      Resource r = addPath(uri);

      return r.getInputStream();
   }

   @Override
   public Resource addPath(String uri) throws IOException {
      Resource resource = m_resources.get(uri);

      // 1. try to find from doc root
      if (resource == null) {
         File file = new File(m_docRoot, uri);

         if (file.exists()) {
            try {
               resource = new FileResource(file.toURI().toURL());

               m_resources.putIfAbsent(uri, resource);
            } catch (URISyntaxException e) {
               // ignore it
            }
         }
      }

      // 2. try to find from web modules
      if (resource == null) {
         URL url = m_manager.getResourceUrl(uri);

         if (url != null) {
            try {
               String protocol = url.getProtocol();

               if (protocol.equals("file")) {
                  resource = new FileResource(url);
                  m_resources.putIfAbsent(uri, resource);
               } else if (protocol.equals("jar")) {
                  resource = new URLResource(url, null, true) {
                  };
                  m_resources.putIfAbsent(uri, resource);
               }
            } catch (URISyntaxException e) {
               // ignore it
            }
         }
      }

      if (resource != null) {
         return resource;
      } else {
         return super.addPath(uri);
      }
   }
}
