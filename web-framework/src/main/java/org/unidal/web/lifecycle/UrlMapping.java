package org.unidal.web.lifecycle;

public interface UrlMapping {
   public String getAction();

   public String getContextPath();

   public String getModule();

   public String getPathInfo();

   public String getQueryString();

   public String getServletPath();
}