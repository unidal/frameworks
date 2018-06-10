package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;

import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.URLResource;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.JarScanner;
import org.unidal.helper.Scanners.ZipEntryMatcher;

public class WebModuleManager {
   private Map<String, URL> m_urls = new HashMap<String, URL>();

   private ConcurrentMap<String, Resource> m_resources = new ConcurrentHashMap<String, Resource>();

   public WebModuleManager() throws Exception {
      List<URL> webModules = Collections.list(getClass().getClassLoader().getResources("META-INF/resources"));

      for (URL webModule : webModules) {
         prepareResources(webModule);
      }
   }

   public Resource getFallbackResource(String uri) {
      Resource resource = m_resources.get(uri);

      if (resource == null) {
         URL url = m_urls.get(uri);

         if (url != null) {
            try {
               String protocol = url.getProtocol();

               if (protocol.equals("file")) {
                  resource = new FileResource(url);
                  m_resources.put(uri, resource);
               } else if (protocol.equals("jar")) {
                  resource = new URLResource(url, null, true) {
                  };
                  m_resources.put(uri, resource);
               }
            } catch (Exception e) {
               // ignore it
               e.printStackTrace();
            }
         }
      }

      return resource;
   }

   public URL getResourceUrl(String uri) {
      return m_urls.get(uri);
   }

   private void prepareResources(URL webModule) throws IOException {
      String protocol = webModule.getProtocol();

      if ("jar".equals(protocol)) {
         String path = webModule.getPath();
         int pos = path.indexOf('!');
         File base = new File(path.substring("file:".length(), pos));
         final String prefix = path.substring(pos + 2) + "/";
         JarScanner scanner = Scanners.forJar();

         List<String> entries = scanner.scan(base, new ZipEntryMatcher() {
            @Override
            public Direction matches(ZipEntry entry, String path) {
               if (path.startsWith(prefix)) {
                  return Direction.MATCHED;
               }

               return Direction.DOWN;
            }
         });

         for (String entry : entries) {
            URL url = new URL("jar:file:" + base + "!/" + entry);

            put(entry.substring(prefix.length() - 1), url);
         }
      } else {
         File base = new File(webModule.getPath());
         final List<String> paths = new ArrayList<String>();

         Scanners.forDir().scan(base, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (new File(base, path).isDirectory()) {
                  return Direction.DOWN;
               } else {
                  paths.add(path);
                  return Direction.MATCHED;
               }
            }
         });

         for (String path : paths) {
            URL url = new URL("file:" + base + "/" + path);

            put("/" + path, url);
         }
      }
   }

   private void put(String uri, URL url) {
      m_urls.put(uri, url);
   }
}
