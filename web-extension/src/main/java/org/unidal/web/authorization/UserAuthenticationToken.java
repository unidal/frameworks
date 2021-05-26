package org.unidal.web.authorization;

import org.apache.shiro.authc.AuthenticationToken;

public class UserAuthenticationToken implements AuthenticationToken {
   private static final long serialVersionUID = 1L;

   private String m_username;

   private String m_password;

   public UserAuthenticationToken(String username, String password) {
      m_username = username;
      m_password = password;
   }

   @Override
   public Object getCredentials() {
      return m_password;
   }

   @Override
   public Object getPrincipal() {
      return m_username;
   }

   @Override
   public String toString() {
      return String.format("%s[%s,%s]", getClass().getSimpleName(), m_username, m_password);
   }
}
