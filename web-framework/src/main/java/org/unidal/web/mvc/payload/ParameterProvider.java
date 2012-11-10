package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

public interface ParameterProvider {
   public InputStream getFile(String name) throws IOException;

   public String[] getParameterNames();
   
   public String getParameter(String name);

   public String[] getParameterValues(String name);
   
   public HttpServletRequest getRequest();
   
   public void setRequest(HttpServletRequest request);
}
