package org.unidal.test.browser;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.env.Platform;

public class BrowserTest extends ComponentTestCase {
   private URL m_url;

   private boolean VERBOSE = true;

   public static void main(String[] args) throws Exception {
      BrowserTest test = new BrowserTest();

      test.setUrl(new URL("http://www.google.com/"));
      test.setUp();
      test.testBrowsers();

      // Browser console = test.lookup(Browser.class, "console");
      // console.display(new URL("http://www.google.com/"));

      Browser browser = test.lookup(Browser.class, "default");
      browser.display(new URL("http://www.google.com/"));
   }

   private void println(String message) {
      if (VERBOSE) {
         System.out.println(message);
      }
   }

   public void setUrl(URL url) {
      m_url = url;
   }

   private void testBrowser(BrowserType browserType) throws Exception {
      Browser browser = lookup(Browser.class, browserType.getId());

      if (browser.isAvailable()) {
         String[] commandLine = ((AbstractBrowser) browser).getCommandLine("");

         println(browserType.getName() + " is availble at " + commandLine[0]);

         if (m_url != null) {
            browser.display(m_url);
         }
      } else {
         println(browserType.getName() + " is unavailable");
      }
   }

   @Test
   public void testBrowserDefault() throws Exception {
      Browser browser = lookup(Browser.class, "default");
      String url = "http://www.example.com/";
      String[] cmdLine = ((AbstractBrowser) browser).getCommandLine(url);
      int index = 0;

      Assert.assertTrue(browser.isAvailable());

      if (Platform.isWindows()) {
      	Assert.assertEquals("rundll32", cmdLine[index++]);
      	Assert.assertEquals("url.dll,FileProtocolHandler", cmdLine[index++]);
      	Assert.assertEquals(url, cmdLine[index++]);
      } else if (Platform.isMac()) {
      	Assert.assertEquals("open", cmdLine[index++]);
      	Assert.assertEquals(url, cmdLine[index++]);
      }
   }

   @Test
   public void testBrowsers() throws Exception {
      testBrowser(BrowserType.INTERNET_EXPLORER);
      testBrowser(BrowserType.FIREFOX);
      testBrowser(BrowserType.OPERA);
   }
}
