package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.unidal.lookup.annotation.Named;

@Named(type = ParameterProvider.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultParameterProvider implements ParameterProvider {
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
      return null;
   }

   @Override
   public String[] getParameterNames() {
      return new String[0];
   }

   @Override
   public String[] getParameterValues(String name) {
      return new String[0];
   }

   @Override
   public HttpServletRequest getRequest() {
      return m_request;
   }

   @Override
   public DefaultParameterProvider setRequest(HttpServletRequest request) {
      m_request = request;
      return this;
   }
}
