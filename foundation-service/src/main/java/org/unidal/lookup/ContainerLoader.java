package org.unidal.lookup;

import java.io.InputStream;

import org.unidal.lookup.container.MyPlexusContainer;

public class ContainerLoader {
   private static volatile PlexusContainer s_container;

   public static void destroy() {
      if (s_container != null) {
         s_container.dispose();
         s_container = null;
      }
   }

   public static PlexusContainer getDefaultContainer() {
      return getDefaultContainer(null);
   }

   	public static PlexusContainer getDefaultContainer(String configuration) {
		if (s_container == null) {
			synchronized (ContainerLoader.class) {
				if (s_container == null) {
					try {
						if (configuration != null) {
							InputStream in = ContainerLoader.class.getClassLoader().getResourceAsStream(configuration);

							s_container = new MyPlexusContainer(in);
						} else {
							s_container = new MyPlexusContainer();
						}
					} catch (Exception e) {
						throw new RuntimeException("Unable to create Plexus container!", e);
					}
				}
			}
		}

		return s_container;
	}

   static class Key {
      private Class<?> m_role;

      private String m_roleHint;

      private String m_id;

      public Key(Class<?> role, String roleHint, String id) {
         m_role = role;
         m_roleHint = roleHint == null ? "default" : roleHint;
         m_id = id;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Key) {
            Key e = (Key) obj;

            if (e.m_role != m_role) {
               return false;
            }

            if (!e.m_roleHint.equals(m_roleHint)) {
               return false;
            }

            if (!e.m_id.equals(m_id)) {
               return false;
            }

            return true;
         }

         return false;
      }

      @Override
      public int hashCode() {
         int hashCode = 0;

         hashCode = hashCode * 31 + m_role.hashCode();
         hashCode = hashCode * 31 + m_roleHint.hashCode();
         hashCode = hashCode * 31 + m_id.hashCode();

         return hashCode;
      }
   }
}
