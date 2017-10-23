package org.unidal.web;

import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.unidal.test.jetty.JettyServer;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.test.book.BookModule;
import org.unidal.web.test.book.ParametersInterceptor;

public class MvcTest extends JettyServer {
   @Override
   protected void configure(WebAppContext context) throws Exception {
      context.addServlet(new ServletHolder("mvc-servlet", new MVC().setContainer(getContainer())), "/book/*");

      super.configure(context);
   }

   @Override
   protected String getContextPath() {
      return "/";
   }

   @Override
   protected int getServerPort() {
      return 1234;
   }

   @Override
   protected boolean isWebXmlDefined() {
      return false;
   }

   @Test
   public void testAdd() throws Exception {
      define(ParametersInterceptor.class);

      checkRequest("/book/add?id=1&name=first",
            "==>signin==>permission==>interceptor==>" + "doAdd==>transition==>showList[1(first)]");
      checkRequest("/book/add?id=2&name=second",
            "==>signin==>permission==>interceptor==>" + "doAdd==>transition==>showList[1(first), 2(second)]");

      checkRequest("/book/add?id=0&name=zero",
            "==>signin==>permission==>interceptor==>" + "doAdd==>transition==>showAdd");
      checkRequest("/book/add?id=3&name=",
            "==>signin==>permission==>interceptor==>" + "error:Error occured during handling inbound action(add)!");
   }

   @Test
   public void testElse() throws Exception {
      checkRequest("/book/else", "==>signin==>doElse==>no payload==>transition==>showList[]");

      checkRequest("/book/else?id=1", "==>signin==>doElse==>no payload==>transition==>error:No method annotated by @"
            + OutboundActionMeta.class.getSimpleName() + "(unknown) found in " + BookModule.class);
   }

   @Test
   public void testList() throws Exception {
      checkRequest("/book/list", "==>signin==>doList==>transition==>showList[]");
      checkRequest("/book/list/1", "==>signin==>doList==>transition==>showList[]");

      checkRequest("/book/list?id=1", "==>signin==>doList==>error:Error occured during handling transition(default)");
   }
}
