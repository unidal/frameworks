package org.unidal.web.authorization;

import org.apache.shiro.authc.AuthenticationToken;

public class ApplicationAuthenticationToken implements AuthenticationToken {
   private static final long serialVersionUID = 1L;

   private String m_appId;

   private String m_host;

   public ApplicationAuthenticationToken(String appId, String host) {
      m_appId = appId;
      m_host = host;
   }

   @Override
   public Object getCredentials() {
      return m_host;
   }

   @Override
   public Object getPrincipal() {
      return m_appId;
   }

   @Override
   public String toString() {
      return String.format("%s[%s, %s]", getClass().getSimpleName(), m_appId, m_host);
   }
}
