package org.unidal.helper;

import java.net.MalformedURLException;
import java.net.URL;

public class Lookups {
   public static ClasspathLookup fromClasspath() {
      return ClasspathLookup.INSTANCE;
   }

   public enum ClasspathLookup {
      INSTANCE;

      public URL byResource(String resourceName) {
         URL resource = getClass().getResource(resourceName);

         if (resource != null) {
            String path = resource.getPath();
            int pos = path.indexOf('!');

            try {
               return new URL(path.substring(0, pos));
            } catch (MalformedURLException e) {
               // ignore it
            }
         }

         return null;
      }
   }
}
