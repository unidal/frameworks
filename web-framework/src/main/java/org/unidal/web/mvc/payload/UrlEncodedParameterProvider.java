package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class UrlEncodedParameterProvider implements ParameterProvider {
   private HttpServletRequest m_request;

   public UrlEncodedParameterProvider() {
   }

   public UrlEncodedParameterProvider(HttpServletRequest request) {
      m_request = request;
   }

   public InputStream getFile(String name) throws IOException {
      throw new UnsupportedOperationException("File upload is only support in multipart/form-data encoding type.");
   }

   public String getParameter(String name) {
      return m_request.getParameter(name);
   }

   @SuppressWarnings("unchecked")
   public String[] getParameterNames() {
      Map<String, String[]> map = m_request.getParameterMap();
      int size = map.size();
      String[] names = new String[size];
      int index = 0;

      for (String name : map.keySet()) {
         names[index++] = name;
      }

      return names;
   }

   public String[] getParameterValues(String name) {
      return m_request.getParameterValues(name);
   }

   public HttpServletRequest getRequest() {
      return m_request;
   }

   public void setRequest(HttpServletRequest request) {
      m_request = request;
   }
}
