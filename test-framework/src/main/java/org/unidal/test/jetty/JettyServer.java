package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.ContainerLoader;
import org.unidal.lookup.PlexusContainer;
import org.unidal.test.browser.Browser;
import org.unidal.test.browser.BrowserManager;

public abstract class JettyServer extends ComponentTestCase {
   private Server m_server;

   private WebModuleResource m_resource;

   protected void checkRequest(String uri, String expected) throws Exception {
      Browser browser = lookup(Browser.class, "memory");
      String contextPath = getContextPath();
      URL url;

      if (contextPath == null || contextPath.equals("/")) {
         url = new URL(String.format("http://localhost:%s%s", getServerPort(), uri));
      } else {
         url = new URL(String.format("http://localhost:%s%s%s", getServerPort(), contextPath, uri));
      }

      browser.display(url);

      Assert.assertEquals(expected, browser.toString());
   }

   protected void configure(WebAppContext context) throws Exception {
      File warRoot = getWarRoot();

      m_resource = new WebModuleResource(warRoot);

      context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
      context.setContextPath(getContextPath());
      context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
      context.setBaseResource(m_resource);
   }

   protected void display(String requestUri) throws Exception {
      BrowserManager manager = lookup(BrowserManager.class);
      StringBuilder sb = new StringBuilder(256);

      sb.append("http://localhost:").append(getServerPort()).append(requestUri);
      manager.display(new URL(sb.toString()));
   }

   @Override
   protected PlexusContainer getContainer() {
      return ContainerLoader.getDefaultContainer();
   }

   protected abstract String getContextPath();

   protected abstract int getServerPort();

   protected File getWarRoot() {
      String warRoot = System.getProperty("warRoot");

      if (warRoot != null) {
         return new File(warRoot);
      } else if (isWebXmlDefined()) {
         return new File("src/main/webapp");
      } else {
         // try to mock the web.xml
         File tmpWar = new File("target/tmp-war");
         File webXmlFile = new File(tmpWar, "WEB-INF/web.xml");
         String webXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
               + "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
               + "   xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n"
               + "   version=\"2.5\">\n" + //
               "</web-app>";

         try {
            Files.forIO().writeTo(webXmlFile, webXml);
         } catch (IOException e) {
            throw new RuntimeException("Unable to create a temporary web.xml.", e);
         }

         return tmpWar;
      }
   }

   protected boolean isWebXmlDefined() {
      return true;
   }

   protected void postConfigure(WebAppContext context) {
      // to be overridden
   }

   @Before
   public void setUp() throws Exception {
      super.setUp();

      startServer();
   }

   protected void startServer() throws Exception {
      Server server = new Server(getServerPort());
      WebAppContext context = new ResourceFallbackWebAppContext();

      configure(context);

      server.setHandler(context);
      server.start();

      context.addServlet(new ServletHolder(new WebModuleServlet(m_resource)), "/");

      postConfigure(context);

      m_server = server;
   }

   protected void stopServer() throws Exception {
      if (m_server != null) {
         m_server.stop();
      }
   }

   @After
   public void tearDown() throws Exception {
      stopServer();

      super.tearDown();
   }

   protected void waitForAnyKey() throws IOException {
      String timestamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());

      System.out.println(String.format("[%s] [INFO] Press ENTER to stop server ... ", timestamp));
      System.in.read();
   }
}
