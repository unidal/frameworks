package org.unidal.test.browser;

import java.io.File;

import org.unidal.lookup.annotation.Named;
import org.unidal.test.env.Platform;

@Named(type = Browser.class, value = "opera")
public class OperaBrowser extends AbstractBrowser {
   private File getInstallPath() {
      return Platform.getProgramFile("opera/opera.exe");
   }

   @Override
   public String[] getCommandLine(String url) {
      return new String[] { getInstallPath().toString(), url };
   }

   public boolean isAvailable() {
      return getInstallPath().exists();
   }

   public BrowserType getId() {
      return BrowserType.OPERA;
   }
}
