package org.unidal.lookup.container;

public class ComponentKey {
   private String m_role;

   private String m_roleHint;

   public ComponentKey(Class<?> type, String roleHint) {
      this(type.getName(), roleHint);
   }

   public ComponentKey(String role, String roleHint) {
      m_role = role;
      m_roleHint = normalize(roleHint);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ComponentKey) {
         ComponentKey o = (ComponentKey) obj;

         return o.m_role.equals(m_role) && o.m_roleHint.equals(m_roleHint);
      }

      return false;
   }

   public String getRole() {
      return m_role;
   }

   public String getRoleHint() {
      return m_roleHint;
   }

   @Override
   public int hashCode() {
      return m_role.hashCode() * 31 + m_roleHint.hashCode();
   }

   public boolean matches(String role, String roleHint) {
      if (m_role.equals(role)) {
         return m_roleHint.equals(normalize(roleHint));
      } else {
         return false;
      }
   }

   private String normalize(String roleHint) {
      if (roleHint == null || roleHint.length() == 0) {
         return "default";
      } else {
         return roleHint;
      }
   }

   @Override
   public String toString() {
      return String.format("%s[role=%s, roleHint=%s]", getClass().getSimpleName(), m_role, m_roleHint);
   }
}
