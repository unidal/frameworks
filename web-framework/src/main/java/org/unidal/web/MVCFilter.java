package org.unidal.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MVCFilter implements Filter {
   private FilterConfig m_config;

   private MVC m_mvc;

   @Override
   public void init(FilterConfig config) throws ServletException {
      m_config = config;
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      if (m_mvc == null) {
         synchronized (this) {
            m_mvc = (MVC) m_config.getServletContext().getAttribute(MVC.ID);

            if (m_mvc == null) {
               throw new ServletException("MVC is not initialized correctly! Please add load-on-startup for MVC servlet.");
            }
         }
      }

      m_mvc.service(request, response);
   }

   @Override
   public void destroy() {
      m_mvc = null;
   }
}
