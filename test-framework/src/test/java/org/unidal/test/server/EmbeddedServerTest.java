package org.unidal.test.server;

import java.io.IOException;
import java.net.URL;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mortbay.log.Log;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.browser.Browser;

public class EmbeddedServerTest extends ComponentTestCase {
	@Test
   public void testServer() throws Exception {
      Log.setLog(null);

      final EmbeddedServer server = EmbeddedServerManager.create(2000);
      final String message = "In method service() of test-servlet";

      server.addServlet(new GenericServlet() {
         private static final long serialVersionUID = 1L;

         @Override
         public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            res.getWriter().write(message);
         }
      }, "test-servlet", "/*");
      server.start(1);

      final Browser browser = lookup(Browser.class, "memory");
      
      browser.display(new URL(server.getBaseUrl()));
      server.join();
      Assert.assertEquals(message, browser.toString());
   }
}
