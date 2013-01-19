package org.unidal.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.initialization.DefaultModuleContext;
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
      String catClientXml = config.getInitParameter("cat-client-xml");
      String initModules = config.getInitParameter("init-modules");

      getLogger().info("MVC is starting at " + config.getServletContext().getContextPath());

      if (!"false".equals(initModules)) {
         initializeModules();
      }

      Cat.initialize(getContainer(), catClientXml == null ? null : new File(catClientXml));

      m_handler = lookup(RequestLifecycle.class, "mvc");
      m_handler.setServletContext(config.getServletContext());

      config.getServletContext().setAttribute(ID, this);
      getLogger().info("MVC started at " + config.getServletContext().getContextPath());
   }

   private void initializeModules() throws ServletException {
      try {
         ModuleContext ctx = new DefaultModuleContext(getContainer());
         ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);

         initializer.execute(ctx);
      } catch (Exception e) {
         throw new ServletException(e);
      }
   }

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      request.setCharacterEncoding("UTF-8");
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
