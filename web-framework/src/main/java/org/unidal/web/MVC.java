package org.unidal.web;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.initialization.ModuleContext;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.web.lifecycle.RequestLifecycle;

import com.dianping.cat.Cat;

public class MVC extends AbstractContainerServlet {
   public static final String ID = "mvc-servlet";

   private static final long serialVersionUID = 1L;

   private RequestLifecycle m_handler;

   @Override
   protected void initComponents(ServletConfig config) throws Exception {
      String contextPath = config.getServletContext().getContextPath();
      String path = contextPath == null || contextPath.length() == 0 ? "/" : contextPath;

      getLogger().info("MVC is starting at " + path);

      initializeCat(config);
      initializeModules(config);

      m_handler = lookup(RequestLifecycle.class, "mvc");
      m_handler.setServletContext(config.getServletContext());

      config.getServletContext().setAttribute(ID, this);
      getLogger().info("MVC started at " + path);
   }

   private void initializeCat(ServletConfig config) {
      String catClientXml = config.getInitParameter("cat-client-xml");
      File clientXmlFile;

      if (catClientXml == null) {
         clientXmlFile = new File(Cat.getCatHome(), "config/client.xml");
      } else if (catClientXml.startsWith("/")) {
         clientXmlFile = new File(catClientXml);
      } else {
         clientXmlFile = new File(Cat.getCatHome(), catClientXml);
      }

      Cat.initialize(getContainer(), clientXmlFile);
   }

   @SuppressWarnings("unchecked")
   private void initializeModules(ServletConfig config) throws ServletException {
      String initModules = config.getInitParameter("init-modules");

      if (!"false".equals(initModules)) {
         try {
            ModuleContext ctx = getContainer().lookup(ModuleContext.class);
            ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);
            Enumeration<String> names = config.getInitParameterNames();

            while (names.hasMoreElements()) {
               String name = names.nextElement();
               String value = config.getInitParameter(name);

               ctx.setAttribute(name, value);
            }

            initializer.execute(ctx);
         } catch (Exception e) {
            throw new ServletException(e);
         }
      }
   }

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (request.getCharacterEncoding() == null) {
         request.setCharacterEncoding("UTF-8");
      }

      response.setContentType("text/html;charset=UTF-8");

      try {
         m_handler.handle(request, response);
      } catch (Throwable t) {
         String message = "Error occured when handling uri: " + request.getRequestURI();

         getLogger().error(message, t);

         if (!response.isCommitted()) {
            response.sendError(500, message);
         }
      }
   }
}
