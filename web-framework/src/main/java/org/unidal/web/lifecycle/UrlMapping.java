package org.unidal.web.lifecycle;

public interface UrlMapping {
   public String getAction();

   public String getContextPath();

   public String getModule();

   public String getPathInfo();

   public String getQueryString();

   public String getRawAction();

   public String getRawModule();

   public String getServletPath();

   public void setAction(String action);

   public void setModule(String module);
}