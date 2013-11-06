package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.DefaultContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.helper.Files;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.ContainerLoader;
import org.unidal.test.browser.BrowserManager;

public abstract class JettyServer extends ContainerHolder {
   private Server m_server;

   @SuppressWarnings("unchecked")
   protected void configure(WebAppContext context) {
      File warRoot = getWarRoot();

      context.getInitParams().put("org.mortbay.jetty.servlet.Default.dirAllowed", "false");
      context.setContextPath(getContextPath());
      context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
      context.setResourceBase(warRoot.getPath());
   }

   protected void display(String requestUri) throws Exception {
      StringBuilder sb = new StringBuilder(256);
      PlexusContainer container = ContainerLoader.getDefaultContainer();
      BrowserManager manager = container.lookup(BrowserManager.class);

      sb.append("http://localhost:").append(getServerPort()).append(requestUri);

      try {
         manager.display(new URL(sb.toString()));
      } finally {
         container.release(manager);
      }
   }

   protected abstract String getContextPath();

   protected abstract int getServerPort();

   protected File getWarRoot() {
      if (isWebXmlDefined()) {
         return new File("src/main/webapp");
      } else {
         // try to mock the web.xml
         File tmpWar = new File("target/tmp-war");
         File webXmlFile = new File(tmpWar, "WEB-INF/web.xml");
         String webXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + //
               "   xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n" + //
               "   version=\"2.5\">\n" + //
               "</web-app>";

         try {
            webXmlFile.getParentFile().mkdirs();
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

   protected void setupContainer() throws Exception {
      PlexusContainer container = ContainerLoader.getDefaultContainer();
      DefaultContext context = new DefaultContext();

      context.put("plexus", container);
      contextualize(context);
   }

   protected void startServer() throws Exception {
      setupContainer();

      Server server = new Server(getServerPort());
      WebAppContext context = new WebAppContext();

      configure(context);
      server.addHandler(context);
      server.start();
      postConfigure(context);

      m_server = server;
   }

   protected void stopServer() throws Exception {
      m_server.stop();
   }

   protected void waitForAnyKey() throws IOException {
      String timestamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());

      System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", timestamp));
      System.in.read();
   }
}
