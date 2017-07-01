package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.unidal.lookup.annotation.Named;

@Named(type = ParameterProvider.class, value = "application/x-www-form-urlencoded", instantiationStrategy = Named.PER_LOOKUP)
public class UrlEncodedParameterProvider implements ParameterProvider {
   private HttpServletRequest m_request;

   @Override
   public InputStream getFile(String name) throws IOException {
      throw new UnsupportedOperationException("File upload is only support in multipart/form-data encoding type.");
   }

   @Override
   public String getModuleName() {
      final String path = m_request.getServletPath();

      if (path != null && path.length() > 0) {
         int index = path.indexOf('/', 1);

         if (index > 0) {
            return path.substring(1, index);
         } else {
            return path.substring(1);
         }
      }

      return "default";
   }

   @Override
   public String getParameter(String name) {
      return m_request.getParameter(name);
   }

   @Override
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

   @Override
   public String[] getParameterValues(String name) {
      return m_request.getParameterValues(name);
   }

   @Override
   public HttpServletRequest getRequest() {
      return m_request;
   }

   @Override
   public UrlEncodedParameterProvider setRequest(HttpServletRequest request) {
      m_request = request;
      return this;
   }
}
