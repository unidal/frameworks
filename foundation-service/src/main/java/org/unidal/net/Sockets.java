package org.unidal.net;

import java.util.ArrayList;
import java.util.List;

public class Sockets {
   public static Client asClient() {
      return new Client();
   }

   public static Server asServer() {
      return new Server();
   }

   /**
    * @deprecated kept for backward compatibility 
    * @return
     */
   @Deprecated
   public static SocketClient forClient() {
      return new SocketClient();
   }

   /**
    * @deprecated kept for backward compatibility 
    * @return
     */
   @Deprecated
   public static SocketServer forServer() {
      return new SocketServer();
   }

   public static class Client {
      private List<Integer> m_ports = new ArrayList<Integer>();

      private List<String> m_servers = new ArrayList<String>();

      private SocketClientManager m_client;

      private int m_maxThreads;

      private String m_threadNamePrefix;

      private int m_checkInterval = 1000; // 1 second by default

      public Client checkInterval(int checkInterval) {
         if (checkInterval > 0) {
            m_checkInterval = checkInterval;
         }

         return this;
      }

      public Client connectTo(String server, int port) {
         m_ports.add(port);
         m_servers.add(server);
         return this;
      }

      public void shutdown() {
         m_client.shutdown();
      }

      public Client start(SocketHandler handler) {
         m_client = new SocketClientManager(handler, m_ports, m_servers);
         m_client.setThreadNamePrefix(m_threadNamePrefix);
         m_client.setMaxThreads(m_maxThreads);
         m_client.setCheckInterval(m_checkInterval);

         m_client.start();
         return this;
      }

      public Client threads(String threadNamePrefix, int maxThreads) {
         m_threadNamePrefix = threadNamePrefix;
         m_maxThreads = maxThreads;
         return this;
      }
   }

   public static class Server {
      private String m_host;

      private int m_port;

      private SocketServerManager m_server;

      private int m_maxThreads;

      private String m_threadNamePrefix;

      public Server listenOn(int port) {
         m_port = port;
         return this;
      }

      public Server listenOn(String host, int port) {
         m_host = host;
         m_port = port;
         return this;
      }

      public void shutdown() {
         if (m_server == null) {
            throw new IllegalStateException("Socket server is not started yet!");
         }

         m_server.shutdown();
      }

      public Server start(SocketHandler handler) {
         m_server = new SocketServerManager(handler, m_port, m_host);
         m_server.setThreadNamePrefix(m_threadNamePrefix);
         m_server.setMaxThreads(m_maxThreads);
         m_server.start();
         return this;
      }

      public Server threads(String threadNamePrefix, int maxThreads) {
         m_threadNamePrefix = threadNamePrefix;
         m_maxThreads = maxThreads;
         return this;
      }
   }

    /**
     * @deprecated kept for backward compatibility 
     */
   @Deprecated
   public static class SocketClient {
      private int m_port;

      private String[] m_servers;

      private MessageSender m_sender;

      private int m_maxThreads;

      private String m_threadNamePrefix;

      public SocketClient connectTo(int port, String... servers) {
         m_port = port;
         m_servers = servers;
         return this;
      }

      public void shutdown() {
         m_sender.shutdown();
      }

      public SocketClient start(MessageDelegate delegate) {
         m_sender = new MessageSender(delegate, m_port, m_servers);
         m_sender.setThreadNamePrefix(m_threadNamePrefix);
         m_sender.setMaxThreads(m_maxThreads);
         m_sender.startClient();
         return this;
      }

      public SocketClient threads(String threadNamePrefix, int maxThreads) {
         m_threadNamePrefix = threadNamePrefix;
         m_maxThreads = maxThreads;
         return this;
      }
   }

   /**
    * @deprecated kept for backward compatibility 
    */
   @Deprecated
   public static class SocketServer {
      private String m_host;

      private int m_port;

      private MessageReceiver m_receiver;

      private int m_maxThreads;

      private String m_threadNamePrefix;

      public SocketServer listenOn(int port) {
         m_port = port;
         return this;
      }

      public SocketServer listenOn(String host, int port) {
         m_host = host;
         m_port = port;
         return this;
      }

      public void shutdown() {
         if (m_receiver == null) {
            throw new IllegalStateException("Socket server is not started yet!");
         }

         m_receiver.shutdown();
      }

      public SocketServer start(MessageDelegate delegate) {
         m_receiver = new MessageReceiver(delegate, m_port, m_host);
         m_receiver.setThreadNamePrefix(m_threadNamePrefix);
         m_receiver.setMaxThreads(m_maxThreads);
         m_receiver.startServer();
         return this;
      }

      public SocketServer threads(String threadNamePrefix, int maxThreads) {
         m_threadNamePrefix = threadNamePrefix;
         m_maxThreads = maxThreads;
         return this;
      }
   }
}
