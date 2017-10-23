package org.unidal.test.browser;

import java.io.File;

import org.unidal.lookup.annotation.Named;
import org.unidal.test.env.Platform;

@Named(type = Browser.class, value = "firefox")
public class FirefoxBrowser extends AbstractBrowser {
   private File getInstallPath() {
      return Platform.getProgramFile("mozilla firefox/firefox.exe");
   }

   @Override
   public String[] getCommandLine(String url) {
      return new String[] { getInstallPath().toString(), url };
   }

   public boolean isAvailable() {
      return getInstallPath().exists();
   }

   public BrowserType getId() {
      return BrowserType.FIREFOX;
   }
}
