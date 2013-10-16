package org.unidal.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.unidal.helper.Matchers;
import org.unidal.helper.Matchers.StringTrie;
import org.unidal.helper.Matchers.TrieHandler;
import org.unidal.helper.Splitters;

public class MVCFilter implements Filter, TrieHandler {
   private MVC m_mvc;

   private StringTrie m_trie;

   @Override
   public void destroy() {
      m_mvc = null;
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest) request;

      try {
         if (m_trie != null) {
            boolean excluded = m_trie.handle(req.getServletPath(), request, response, chain);

            if (!excluded) {
               m_mvc.service(request, response);
            }
         } else {
            m_mvc.service(request, response);
         }
      } catch (IOException e) {
         throw e;
      } catch (ServletException e) {
         throw e;
      } catch (Exception e) {
         // TODO custom error page
         throw new ServletException(String.format("Error when handling page(%s)!", req.getRequestURI()), e);
      }
   }

   @Override
   public void handle(String str, int start, int end, boolean prefixOrSuffix, Object[] arguments) throws IOException,
         ServletException {
      ServletRequest request = (ServletRequest) arguments[0];
      ServletResponse response = (ServletResponse) arguments[1];
      FilterChain chain = (FilterChain) arguments[2];

      chain.doFilter(request, response); // pass to next filter
   }

   @Override
   public void init(FilterConfig config) throws ServletException {
      m_mvc = new MVC();
      m_mvc.init(new ServletConfigAdaptor(config));

      String excludes = config.getInitParameter("excludes");

      if (excludes != null) {
         initExcludes(excludes);
      }
   }

   private void initExcludes(String excludes) {
      List<String> parts = Splitters.by(',').noEmptyItem().trim().split(excludes);

      m_trie = Matchers.forTrie();

      for (String part : parts) {
         if (part.endsWith("*")) {
            m_trie.addHandler(part.substring(0, part.length() - 2), this, true);
         } else if (part.startsWith("*")) {
            m_trie.addHandler(part.substring(1), this, false);
         } else {
            m_trie.addHandler(part, this, true);
         }
      }
   }

   static class ServletConfigAdaptor implements ServletConfig {
      private FilterConfig m_config;

      public ServletConfigAdaptor(FilterConfig config) {
         m_config = config;
      }

      @Override
      public String getInitParameter(String name) {
         return m_config.getInitParameter(name);
      }

      @Override
      @SuppressWarnings("rawtypes")
      public Enumeration getInitParameterNames() {
         return m_config.getInitParameterNames();
      }

      @Override
      public ServletContext getServletContext() {
         return m_config.getServletContext();
      }

      @Override
      public String getServletName() {
         return m_config.getFilterName();
      }
   }
}
