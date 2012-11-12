package org.unidal.test.server;

import java.io.IOException;
import java.util.Map;

import javax.el.ELResolver;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.ErrorHandler;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

public class EmbeddedServer {
   private EmbeddedServerConfig m_config;

   private EmbeddedServerSslConfig m_sslConfig;

   Server m_server;

   Context m_context;

   private int m_servletInitOrder;

   public EmbeddedServer(EmbeddedServerConfig config) {
      this(config, null);
   }

   public EmbeddedServer(EmbeddedServerConfig config, EmbeddedServerSslConfig sslConfig) {
      m_config = config;
      m_sslConfig = sslConfig;

      initialize();
   }

   public void addELResolver(ELResolver resolver) {
      ServletContext sc = m_context.getServletContext();
      JspApplicationContext ctx = JspFactory.getDefaultFactory().getJspApplicationContext(sc);
      
      ctx.addELResolver(resolver);
   }
   
   public void addFilter(Class<? extends Filter> clazz, String name, String pathSpec) {
      addFilter(clazz, name, pathSpec, null);
   }

   public void addFilter(Class<? extends Filter> clazz, String name, String pathSpec, Map<String, String> initParameters) {
      FilterHolder filterHolder = new FilterHolder(clazz);

      addFilterHolder(filterHolder, name, pathSpec, initParameters);
   }

   public void addFilter(Filter filter, String name, String pathSpec) {
      addFilter(filter, name, pathSpec, null);
   }

   public void addFilter(Filter filter, String name, String pathSpec, Map<String, String> initParameters) {
      FilterHolder filterHolder = new FilterHolder(filter);

      addFilterHolder(filterHolder, name, pathSpec, initParameters);
   }

   private void addFilterHolder(FilterHolder filterHolder, String name, String pathSpec,
         Map<String, String> initParameters) {
      filterHolder.setName(name);
      filterHolder.setInitParameters(initParameters);

      m_context.addFilter(filterHolder, pathSpec, Handler.ALL);
   }

   public void addServlet(Class<? extends Servlet> clazz, String name, String pathSpec) {
      addServlet(clazz, name, pathSpec, null);
   }

   public void addServlet(Class<? extends Servlet> clazz, String name, String pathSpec,
         Map<String, String> initParameters) {
      ServletHolder servletHolder = new ServletHolder(clazz);

      addServletHolder(servletHolder, name, pathSpec, initParameters);
   }

   public void addServlet(Servlet servlet, String name, String pathSpec) {
      addServlet(servlet, name, pathSpec, null);
   }

   public void addServlet(Servlet servlet, String name, String pathSpec, Map<String, String> initParameters) {
      ServletHolder servletHolder = new ServletHolder(servlet);

      addServletHolder(servletHolder, name, pathSpec, initParameters);
   }

   private void addServletHolder(ServletHolder servletHolder, String name, String pathSpec,
         Map<String, String> initParameters) {
      servletHolder.setName(name);
      servletHolder.setInitParameters(initParameters);
      servletHolder.setInitOrder(m_servletInitOrder++);

      m_context.getServletHandler().addServletWithMapping(servletHolder, pathSpec);
   }

   public String getBaseUrl() {
      return EmbeddedServerManager.getBaseUrl(m_config.getPort(), null, false);
   }
   
   public String getBaseSecureUrl() {
      return EmbeddedServerManager.getBaseUrl(m_sslConfig.getSslPort(), null, true);
   }

   public EmbeddedServerConfig getConfig() {
      return m_config;
   }

   public Filter getFilter(String name) {
      FilterHolder filterHolder = m_context.getServletHandler().getFilter(name);

      if (filterHolder == null) {
         throw new IllegalArgumentException("No Fileter(" + name + ") registered.");
      } else {
         return filterHolder.getFilter();
      }
   }

   public int getPort() {
      return m_config.getPort();
   }

   public Servlet getServlet(String name) throws ServletException {
      ServletHolder servletHolder = m_context.getServletHandler().getServlet(name);

      if (servletHolder == null) {
         throw new IllegalArgumentException("No Servlet(" + name + ") registered.");
      } else {
         return servletHolder.getServlet();
      }
   }

   public ServletContext getServletContext() {
      return m_context.getServletContext();
   }

   private void initialize() {
      ContextHandlerCollection contexts = new ContextHandlerCollection();
      int port = EmbeddedServerManager.getNextAvailablePort(m_config.getPort(), 100);

      m_config.setPort(port);
      m_server = new Server(port);
      m_server.setStopAtShutdown(true);
      m_server.setSendServerVersion(true);
      m_server.setSendDateHeader(true);
      m_server.setHandler(contexts);

      if (m_sslConfig != null) {
         SslSocketConnector sslConnector = new SslSocketConnector();

         sslConnector.setPort(m_sslConfig.getSslPort());

         if (m_sslConfig.getSslKeystore() != null) {
            sslConnector.setKeystore(m_sslConfig.getSslKeystore());
         }

         if (m_sslConfig.getSslPassword() != null) {
            sslConnector.setPassword(m_sslConfig.getSslPassword());
         }

         if (m_sslConfig.getSslKeyPassword() != null) {
            sslConnector.setKeyPassword(m_sslConfig.getSslKeyPassword());
         }

         m_server.addConnector(sslConnector);
      }

      m_context = new Context(contexts, m_config.getContextPath(), Context.SESSIONS);
      m_context.setResourceBase(m_config.getResourceBase());
      m_context.setClassLoader(m_config.getClassLoader());
   }

   public boolean isStarted() {
      return m_server.isStarted();
   }

   public boolean isStopped() {
      return m_server.isStopped();
   }

   public void join() {
      try {
         m_server.join();
      } catch (InterruptedException e) {
         // ignore it
      }
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      m_context.setErrorHandler(errorHandler);
   }

   public void start() {
      try {
         m_server.start();
      } catch (Exception e) {
         throw new RuntimeException("Can't start the server.", e);
      }
   }

   public void start(int requestsBeforeAutoStop) {
      try {
         m_server.start();
      } catch (Exception e) {
         throw new RuntimeException("Can't start the server.", e);
      }

      if (requestsBeforeAutoStop > 0) {
         new WatchDog(requestsBeforeAutoStop).start();
      }
   }

   public void stop() {
      try {
         m_server.stop();
      } catch (Exception e) {
         // do nothing;
      }
   }

   private static final class CountDownFilter implements Filter {
      private int m_count;

      public CountDownFilter(int maxCount) {
         m_count = maxCount;
      }

      public void destroy() {
      }

      public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
         chain.doFilter(request, response);
         response.flushBuffer();
         m_count--;
      }

      public void init(FilterConfig filterConfig) throws ServletException {
      }

      public boolean isCountUsedUp() {
         return m_count <= 0;
      }
   }

   private final class WatchDog extends Thread {
      private CountDownFilter m_countDownFilter;

      public WatchDog(int requestsBeforeDie) {
         m_countDownFilter = new CountDownFilter(requestsBeforeDie);

         addFilter(m_countDownFilter, "CountDownFilter", "/*");
      }

      @Override
      public void run() {
         try {
            while (!m_countDownFilter.isCountUsedUp()) {
               Thread.sleep(10);
            }

            m_server.stop();
         } catch (Exception e) {
            // ignore it
         }
      }
   }
}
