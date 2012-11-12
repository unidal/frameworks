package org.unidal.test.server;

public class EmbeddedServerSslConfig {
   private int m_sslPort;

   private String m_sslKeystore;

   private String m_sslPassword;

   private String m_sslKeyPassword;

   public EmbeddedServerSslConfig(int sslPort, String sslKeystore, String sslPassword, String sslKeyPassword) {
      m_sslPort = sslPort;
      m_sslKeystore = sslKeystore;
      m_sslPassword = sslPassword;
      m_sslKeyPassword = sslKeyPassword;
   }

   protected String getSslKeyPassword() {
      return m_sslKeyPassword;
   }

   protected String getSslKeystore() {
      return m_sslKeystore;
   }

   protected String getSslPassword() {
      return m_sslPassword;
   }

   protected int getSslPort() {
      return m_sslPort;
   }

   protected void setSslKeyPassword(String sslKeyPassword) {
      m_sslKeyPassword = sslKeyPassword;
   }

   protected void setSslKeystore(String sslKeystore) {
      m_sslKeystore = sslKeystore;
   }

   protected void setSslPassword(String sslPassword) {
      m_sslPassword = sslPassword;
   }

   protected void setSslPort(int sslPort) {
      m_sslPort = sslPort;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(512);

      sb.append("ServletContainerConfig[");
      sb.append("port=").append(m_sslPort);
      sb.append(",keystore=").append(m_sslKeystore);
      sb.append(",password=").append(m_sslPassword);
      sb.append(",keyPassword=").append(m_sslKeyPassword);
      sb.append("]");
      return sb.toString();
   }

}
