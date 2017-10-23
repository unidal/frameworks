package org.unidal.test.browser;

import java.net.URL;
import java.net.URLConnection;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Named;

@Named(type = Browser.class, value = "memory", instantiationStrategy = Named.PER_LOOKUP)
public class MemoryBrowser implements Browser {
   private StringBuilder m_content = new StringBuilder();

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

      if (html != null) {
         m_content.ensureCapacity(html.length());
      }

      m_content.setLength(0);
      m_content.append(html);
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
      return BrowserType.MEMORY;
   }

   public boolean isAvailable() {
      return true;
   }

   public String getContent() {
      return m_content.toString();
   }

   @Override
   public String toString() {
      return m_content.toString();
   }
}
