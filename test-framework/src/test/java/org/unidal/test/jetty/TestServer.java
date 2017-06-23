package org.unidal.test.jetty;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.servlet.GzipFilter;

@RunWith(JUnit4.class)
public class TestServer extends JettyServer {
   public static void main(String[] args) throws Exception {
      TestServer server = new TestServer();
      server.startServer();
      server.startWebApp();
      server.stopServer();
   }

   @Override
   protected String getContextPath() {
      return "/";
   }

   @Override
   protected int getServerPort() {
      return 6625;
   }

   @Override
   protected boolean isWebXmlDefined() {
      return false;
   }

   @Override
   protected void postConfigure(WebAppContext context) {
      context.addServlet(new ServletHolder(new MockServlet()), "/mock/*");
      context.addFilter(GzipFilter.class, "/*", Handler.ALL);
   }

   @Test
   public void startWebApp() throws Exception {
      super.startServer();
      display("/mock");
      waitForAnyKey();
   }

   private class MockServlet extends HttpServlet {
      private static final long serialVersionUID = 1L;

      @Override
      protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
         OutputStream out = res.getOutputStream();

         res.setContentType("text/html");
         out.write("<h2>Hello, World!</h2>".getBytes());
      }
   }
}
