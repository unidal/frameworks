package org.unidal.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.unidal.helper.Files.AutoClose;

public class Urls {
   public static UrlIO forIO() {
      return new UrlIO();
   }

   public static class UrlIO {
      private int m_readTimeout;

      private int m_connectTimeout;

      public UrlIO connectTimeout(int connectTimeout) {
         m_connectTimeout = connectTimeout;
         return this;
      }

      public void copy(String url, OutputStream out) throws IOException {
         Files.forIO().copy(openStream(url), out, AutoClose.INPUT);
      }

      public InputStream openStream(String url) throws IOException {
         URLConnection conn = new URL(url).openConnection();

         if (m_connectTimeout > 0) {
            conn.setConnectTimeout(m_connectTimeout);
         }

         if (m_readTimeout > 0) {
            conn.setReadTimeout(m_readTimeout);
         }

         return conn.getInputStream();
      }

      public UrlIO readTimeout(int readTimeout) {
         m_readTimeout = readTimeout;
         return this;
      }
   }
}
