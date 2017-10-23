package org.unidal.test.browser;

import java.net.URL;
import java.net.URLConnection;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Named;

@Named(type = Browser.class, value = "console")
public class ConsoleBrowser implements Browser {
   private boolean m_silent;

   private String determinCharset(String contentType, String defaultCharset) {
      if (contentType != null) {
         String token = "charset=";
         int index = contentType.toLowerCase().indexOf(token);

         if (index > 0) {
            return contentType.substring(index + token.length()).trim();
         }
      }

      return defaultCharset;
   }

   public void display(String html) {
      display(html, "utf-8");
   }

   public void display(String html, String charset) {
      if (!m_silent) {
         System.out.print(html);
      }
   }

   public void display(URL url) {
      try {
         URLConnection urlc = url.openConnection();
         String contentType = urlc.getHeaderField("Content-Type");
         byte[] ba = Files.forIO().readFrom(urlc.getInputStream());

         display(new String(ba, determinCharset(contentType, "utf-8")));
      } catch (Exception e) {
         throw new RuntimeException("Error when accessing URL: " + url, e);
      }
   }

   public BrowserType getId() {
      return BrowserType.CONSOLE;
   }

   public boolean isAvailable() {
      return true;
   }

   public boolean isSilent() {
      return m_silent;
   }

   public void setSilent(boolean silent) {
      m_silent = silent;
   }
}
