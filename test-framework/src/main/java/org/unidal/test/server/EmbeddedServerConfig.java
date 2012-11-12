package org.unidal.test.server;

public class EmbeddedServerConfig {
   private int m_port;

   private String m_contextPath;

   private String m_resourceBase;

   private ClassLoader m_classLoader;

   public EmbeddedServerConfig(int port, String contextPath, String resourceBase) {
      this(port, contextPath, resourceBase, null);
   }

   public EmbeddedServerConfig(int port, String contextPath, String resourceBase, ClassLoader classLoader) {
      if (port < 0) {
         throw new RuntimeException("Port must be a positive integer");
      }

      m_port = port;
      m_contextPath = contextPath != null ? contextPath : "/";
      m_resourceBase = resourceBase != null ? resourceBase : ".";
      m_classLoader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
   }

   public int getPort() {
      return m_port;
   }

   public String getContextPath() {
      return m_contextPath;
   }

   public String getResourceBase() {
      return m_resourceBase;
   }

   public ClassLoader getClassLoader() {
      return m_classLoader;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(512);

      sb.append("ServletContainerConfig[");
      sb.append("port=").append(m_port);
      sb.append(",contextPath=").append(m_contextPath);
      sb.append(",resourceBase=").append(m_resourceBase);
      sb.append(",classLoader=").append(m_classLoader);
      sb.append("]");
      return sb.toString();
   }

   public void setPort(int port) {
      m_port = port;
   }
}
