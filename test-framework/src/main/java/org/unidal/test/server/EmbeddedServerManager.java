package org.unidal.test.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

public class EmbeddedServerManager {
   private static AtomicReference<String> s_localHostName = new AtomicReference<String>();

   public static EmbeddedServer create(int port) {
      return create(port, null, null);
   }

   public static EmbeddedServer create(int port, String contextPath, String resourceBase) {
      return create(new EmbeddedServerConfig(port, contextPath, resourceBase));
   }

   public static EmbeddedServer create(String contextPath, String resourceBase) {
      return create(getUniquePort(), contextPath, resourceBase);
   }

   public static EmbeddedServer create(EmbeddedServerConfig config) {
      if (config == null) {
         throw new NullPointerException();
      }

      return new EmbeddedServer(config);
   }

   public static String getBaseUrl(int port, String path, boolean secure) {
      StringBuilder sb = new StringBuilder(100);

      sb.append(secure ? "https://" : "http://");
      sb.append(getLocalHostName());

      if (!(secure && port == 443 || !secure && port == 80)) {
         sb.append(':').append(port);
      }

      if (path != null) {
         sb.append(path);
      }

      return sb.toString();
   }

   public static String getLocalHostName() {
      if (s_localHostName.get() == null) {
         try {
            s_localHostName.set(InetAddress.getLocalHost().getCanonicalHostName());
         } catch (UnknownHostException e) {
            s_localHostName.set("localhost");
         }
      }

      return s_localHostName.get();
   }

   public static int getNextAvailablePort(int initialPort, int tries) {
      int port = initialPort;

      for (int i = 1; i <= tries; i++) {
         try {
            ServerSocket s = new ServerSocket(port);

            s.close();
            return port;
         } catch (BindException e) {
            System.err.println("Attempt " + i + " of " + tries + " failed on port " + port);
            port++;
         } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
         }
      }

      throw new RuntimeException("Unable to get port after " + tries + " tries.");
   }

   public static int getUniquePort() {
      String portStr = System.getProperty("server.port");
      int port;

      if (portStr == null) {
         // http://www.iana.org/assignments/port-numbers
         // The Dynamic and/or Private Ports are those from 49152 through 65535
         // 65535 - 49152 = 16383
         port = (int) (Math.random() * 16383.0);
         port += 49152;
      } else {
         port = Integer.parseInt(portStr);
      }

      return port;
   }
}
