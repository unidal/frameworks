package org.unidal.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.helper.Matchers;
import org.unidal.helper.Matchers.StringTrie;
import org.unidal.helper.Matchers.TrieHandler;
import org.unidal.helper.Splitters;

public class MVCFilter implements Filter {
   private MVC m_mvc;

   private StringTrie m_skipCurrents;

   private StringTrie m_skipRests;

   private String m_pathPrefix;

   @Override
   public void destroy() {
      m_mvc = null;
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {
      HttpServletRequest req = (HttpServletRequest) request;
      String uri = getRelativeUri(req);

      try {
         boolean processed = false;

         if (!processed && m_skipRests != null) {
            processed = m_skipRests.handle(uri, request, response, chain);
         }

         if (!processed && m_skipCurrents != null) {
            processed = m_skipCurrents.handle(uri, request, response, chain);
         }

         if (!processed) {
            m_mvc.service(request, response);
         }
      } catch (IOException e) {
         throw e;
      } catch (ServletException e) {
         throw e;
      } catch (Exception e) {
         // TODO custom error page here
         throw new ServletException(String.format("Error when handling page(%s)!", req.getRequestURI()), e);
      }
   }

   @Override
   public void init(FilterConfig config) throws ServletException {
      if (m_mvc == null) {
         m_mvc = new MVC();
         m_mvc.init(new ServletConfigAdaptor(config));

         String skipCurrent = config.getInitParameter("skip-current");
         String skipRest = config.getInitParameter("skip-rest");
         String pathPrefix = config.getInitParameter("path-prefix");

         if (skipCurrent == null) {
            skipCurrent = config.getInitParameter("excludes"); // deprecated
         }

         if (skipCurrent != null) {
            initSkipCurrents(skipCurrent);
         }

         if (skipRest != null) {
            initSkipRests(skipRest);
         }

         m_pathPrefix = pathPrefix;
      }
   }

   private void initSkipCurrents(String excludes) {
      List<String> parts = Splitters.by(',').noEmptyItem().trim().split(excludes);

      m_skipCurrents = Matchers.forTrie();

      for (String part : parts) {
         if (part.endsWith("*")) {
            m_skipCurrents.addHandler(part.substring(0, part.length() - 2), Handlers.SKIP_CURRENT, true);
         } else if (part.startsWith("*")) {
            m_skipCurrents.addHandler(part.substring(1), Handlers.SKIP_CURRENT, false);
         } else {
            m_skipCurrents.addHandler(part, Handlers.SKIP_CURRENT, true);
         }
      }
   }

   private void initSkipRests(String includes) {
      List<String> parts = Splitters.by(',').noEmptyItem().trim().split(includes);

      m_skipRests = Matchers.forTrie();

      for (String part : parts) {
         if (part.endsWith("*")) {
            m_skipRests.addHandler(part.substring(0, part.length() - 2), Handlers.SKIP_REST, true);
         } else if (part.startsWith("*")) {
            m_skipRests.addHandler(part.substring(1), Handlers.SKIP_REST, false);
         } else {
            m_skipRests.addHandler(part, Handlers.SKIP_REST, true);
         }
      }
   }

   private String getRelativeUri(HttpServletRequest req) {
      String contextPath = req.getContextPath();
      String uri = req.getRequestURI();

      if (contextPath != null && contextPath.length() > 1 && uri.startsWith(contextPath)) {
         uri = uri.substring(contextPath.length());
      }

      if (m_pathPrefix != null && uri.startsWith(m_pathPrefix)) {
         uri = uri.substring(m_pathPrefix.length());
      }

      return uri;
   }

   enum Handlers implements TrieHandler {
      SKIP_REST {
         @Override
         public void handle(String str, int start, int end, boolean prefixOrSuffix, Object[] arguments)
               throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) arguments[0];
            HttpServletResponse response = (HttpServletResponse) arguments[1];
            FilterChain chain = (FilterChain) arguments[2];
            String uri = request.getRequestURI();
            String contextPath = request.getContextPath();

            if (contextPath != null && contextPath.length() > 1 && uri.startsWith(contextPath)) {
               uri = uri.substring(contextPath.length());
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(uri);

            if (dispatcher != null) {
               dispatcher.forward(request, response);
            } else {
               chain.doFilter(request, response); // pass to next filter
            }
         }
      },

      SKIP_CURRENT {
         @Override
         public void handle(String str, int start, int end, boolean prefixOrSuffix, Object[] arguments)
               throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) arguments[0];
            HttpServletResponse response = (HttpServletResponse) arguments[1];
            FilterChain chain = (FilterChain) arguments[2];

            chain.doFilter(request, response); // pass to next filter
         }
      };
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
      public Enumeration<String> getInitParameterNames() {
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
