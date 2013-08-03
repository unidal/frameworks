package org.unidal.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Files.AutoClose;

public class Urls {
   public static UrlIO forIO() {
      return new UrlIO();
   }

   public static class UrlIO {
      private int m_readTimeout;

      private int m_connectTimeout;

      private Map<String, String> m_headers = new HashMap<String, String>();

      public UrlIO connectTimeout(int connectTimeout) {
         m_connectTimeout = connectTimeout;
         return this;
      }

      public UrlIO header(String name, String value) {
         if (m_headers == null) {
            m_headers = new HashMap<String, String>();
         }

         m_headers.put(name, value);
         return this;
      }

      public void copy(String url, OutputStream out) throws IOException {
         Files.forIO().copy(openStream(url), out, AutoClose.INPUT);
      }

      public InputStream openStream(String url) throws IOException {
         return openStream(url, null);
      }

      public InputStream openStream(String url, Map<String, List<String>> responseHeaders) throws IOException {
         URLConnection conn = new URL(url).openConnection();

         if (m_connectTimeout > 0) {
            conn.setConnectTimeout(m_connectTimeout);
         }

         if (m_readTimeout > 0) {
            conn.setReadTimeout(m_readTimeout);
         }

         if (m_headers != null) {
            for (Map.Entry<String, String> e : m_headers.entrySet()) {
               conn.setRequestProperty(e.getKey(), e.getValue());
            }
         }

         if (responseHeaders != null) {
            responseHeaders.putAll(conn.getHeaderFields());
         }

         return conn.getInputStream();
      }

      public UrlIO readTimeout(int readTimeout) {
         m_readTimeout = readTimeout;
         return this;
      }
   }
}
