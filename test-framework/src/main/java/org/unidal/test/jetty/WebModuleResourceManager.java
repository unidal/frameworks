package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.JarScanner;

public class WebModuleResourceManager {
   private Map<String, URL> m_resources = new HashMap<String, URL>();

   public WebModuleResourceManager() throws Exception {
      List<URL> webModules = Collections.list(getClass().getClassLoader().getResources("WEB-MODULE"));

      for (URL webModule : webModules) {
         prepareResources(webModule);
      }
   }

   public URL getResource(String uri) {
      return m_resources.get(uri);
   }

   private void prepareResources(URL webModule) throws IOException {
      String protocol = webModule.getProtocol();

      if ("jar".equals(protocol)) {
         String path = webModule.getPath();
         int pos = path.indexOf('!');
         File base = new File(path.substring("file:".length(), pos));
         final String prefix = path.substring(pos + 2) + "/";
         JarScanner scanner = Scanners.forJar();

         List<String> entries = scanner.scan(base, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
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
      m_resources.put(uri, url);
   }
}
