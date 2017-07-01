package org.unidal.test.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

public class TestJetty {
   @Test
   public void test() throws Exception {
      Server server = new Server(8080);
      ContextHandler context = new ContextHandler();

      context.setContextPath("/");
      context.setResourceBase(".");
      context.setClassLoader(Thread.currentThread().getContextClassLoader());
      server.setHandler(context);
      context.setHandler(new HelloHandler());

      server.start();
      server.join();
   }

   @Test
   public void test2() throws Exception {
      Server server = new Server(8080);

      ServletContextHandler root = new ServletContextHandler(null, "/", ServletContextHandler.SESSIONS);

      root.addServlet(new ServletHolder(new HelloServlet()), "/");

      server.setHandler(root);
      server.start();
      server.join();
   }

   static class HelloHandler extends AbstractHandler {
      @Override
      public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
         ServletOutputStream out = response.getOutputStream();

         out.write("Hello, world!".getBytes());
         out.flush();
      }
   }

   public class HelloServlet extends HttpServlet {
      private static final long serialVersionUID = 1L;

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
         ServletOutputStream out = response.getOutputStream();

         out.write("Hello, World!".getBytes());
         out.flush();
      }
   }
}
