package org.unidal.web.lifecycle;

public class DefaultUrlMapping implements UrlMapping {
   private String m_contextPath;

   private String m_servletPath;

   private String m_module;

   private String m_action;

   private String m_rawModule;

   private String m_rawAction;

   private String m_pathInfo;

   private String m_queryString;

   public DefaultUrlMapping(String[] sections) {
      int index = 0;

      m_contextPath = trim(sections[index++]);
      m_servletPath = trim(sections[index++]);
      m_rawModule = trim(sections[index++]);
      m_rawAction = trim(sections[index++]);
      m_pathInfo = trim(sections[index++]);
      m_queryString = sections[index++];
   }

   public DefaultUrlMapping(UrlMapping urlMapping) {
      m_contextPath = urlMapping.getContextPath();
      m_servletPath = urlMapping.getServletPath();
      m_rawModule = urlMapping.getRawModule();
      m_rawAction = urlMapping.getRawAction();
      m_module = urlMapping.getModule();
      m_action = urlMapping.getAction();
      m_pathInfo = urlMapping.getPathInfo();
      m_queryString = urlMapping.getQueryString();
   }

   @Override
   public String getAction() {
      if (m_action != null) {
         return m_action;
      } else {
         return m_rawAction;
      }
   }

   @Override
   public String getContextPath() {
      return m_contextPath;
   }

   @Override
   public String getModule() {
      if (m_module != null) {
         return m_module;
      } else {
         return m_rawModule;
      }
   }

   @Override
   public String getPathInfo() {
      return m_pathInfo;
   }

   @Override
   public String getQueryString() {
      return m_queryString;
   }

   @Override
   public String getRawAction() {
      return m_rawAction;
   }

   @Override
   public String getRawModule() {
      return m_rawModule;
   }

   @Override
   public String getServletPath() {
      return m_servletPath;
   }

   @Override
   public void setAction(String action) {
      m_action = action;
   }

   public void setContextPath(String contextPath) {
      m_contextPath = contextPath;
   }

   @Override
   public void setModule(String module) {
      m_module = module;
   }

   public void setPathInfo(String pathInfo) {
      m_pathInfo = pathInfo;
   }

   public void setQueryString(String queryString) {
      m_queryString = queryString;
   }

   public void setServletPath(String servletPath) {
      m_servletPath = servletPath;
   }

   @Override
   public String toString() {
      return String.format("%s[servletPath=%s, contextPath=%s, module=%s, action=%s, pathInfo=%s, queryString=%s]",
            getClass().getSimpleName(), m_servletPath, m_contextPath, m_module, m_action, m_pathInfo, m_queryString);
   }

   private String trim(String str) {
      if (str != null) {
         int pos = str.indexOf(';');

         if (pos >= 0) {
            return str.substring(0, pos);
         }
      }

      return str;
   }
}
