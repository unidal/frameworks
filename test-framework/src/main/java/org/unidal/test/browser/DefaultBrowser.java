package org.unidal.test.browser;

import org.unidal.lookup.annotation.Named;
import org.unidal.test.env.Platform;

@Named(type = Browser.class, value = "default")
public class DefaultBrowser extends AbstractBrowser {
   @Override
   public String[] getCommandLine(String url) {
      if (Platform.isWindows()) {
         return new String[] { "rundll32", "url.dll,FileProtocolHandler", url };
      } else if (Platform.isMac()) {
         return new String[] { "open", url };
      } else if (Platform.isLinux()) {
         return new String[] { "xdg-open", url };
      } else {
         throw new RuntimeException(String.format("Not supported OS(%s)!", System.getProperty("os.name")));
      }
   }

   public boolean isAvailable() {
      return true;
   }

   public BrowserType getId() {
      return BrowserType.DEFAULT;
   }
}
