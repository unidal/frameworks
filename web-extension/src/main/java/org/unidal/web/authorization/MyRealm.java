package org.unidal.web.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.unidal.helper.Splitters;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class MyRealm extends AuthorizingRealm {
   @Inject
   private MyAuthorization m_authorization;

   @Inject
   private MyApplication m_application;

   @Inject
   private MyUser m_user;

   private Map<String, TulipWildcardPermission> m_cache = new HashMap<String, TulipWildcardPermission>();

   @Override
   protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
      if (token instanceof ApplicationAuthenticationToken) {
         String appId = (String) token.getPrincipal();
         String host = (String) token.getCredentials();

         if (m_application.isMatched(appId, host)) {
            return new SimpleAuthenticationInfo(appId, host, getName());
         }
      } else if (token instanceof UserAuthenticationToken) {
         String username = (String) token.getPrincipal();
         String password = (String) token.getCredentials();

         if (m_user.isMatched(username, password)) {
            return new SimpleAuthenticationInfo(username, password, getName());
         }
      }

      return null;
   }

   @Override
   protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
      if (principals == null) {
         throw new AuthorizationException("No principals specified.");
      }

      String principal = (String) getAvailablePrincipal(principals);
      Set<String> permissions = m_authorization.findPermissionsForApp(principal);
      Set<Permission> objects = new HashSet<Permission>();

      if (permissions.isEmpty()) {
         permissions = m_authorization.findPermissionsForUser(principal);
      }

      for (String permission : permissions) {
         objects.add(findOrCreatePermission(permission));
      }

      SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

      info.setObjectPermissions(objects);
      return info;
   }

   private TulipWildcardPermission findOrCreatePermission(String permission) {
      TulipWildcardPermission perm = m_cache.get(permission);

      if (perm == null) {
         perm = new TulipWildcardPermission(permission);
         m_cache.put(permission, perm);
      }

      return perm;
   }

   @Override
   public boolean supports(AuthenticationToken token) {
      return token instanceof ApplicationAuthenticationToken || token instanceof UserAuthenticationToken;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }

   private class TulipWildcardPermission implements Permission {
      private List<List<String>> m_parts = new ArrayList<List<String>>();

      public TulipWildcardPermission(String permission) {
         List<String> sections = Splitters.by(':').trim().noEmptyItem().split(permission.toLowerCase());

         for (String section : sections) {
            List<String> elements = Splitters.by(',').noEmptyItem().split(section);

            m_parts.add(elements);
         }
      }

      @Override
      public boolean implies(Permission p) {
         // By default only supports comparisons with other MyWildcardPermission
         if (!(p instanceof WildcardPermission)) {
            return false;
         }

         TulipWildcardPermission wp = findOrCreatePermission(p.toString());
         List<List<String>> otherParts = wp.m_parts;
         int i = 0;

         for (List<String> otherPart : otherParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (m_parts.size() - 1 < i) {
               return true;
            } else {
               List<String> part = m_parts.get(i);

               for (String element : part) {
                  if (element.equals("*")) {
                     continue;
                  }

                  for (String otherElement : otherPart) {
                     if (element.startsWith("*") && otherElement.endsWith(element.substring(1))) {
                        continue;
                     } else if (element.endsWith("*") && otherElement.startsWith(element.substring(0, element.length() - 1))) {
                        continue;
                     } else if (element.equals(otherElement)) {
                        continue;
                     } else {
                        return false;
                     }
                  }
               }

               i++;
            }
         }

         // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
         for (; i < m_parts.size(); i++) {
            List<String> part = m_parts.get(i);

            if (!part.contains("*")) {
               return false;
            }
         }

         return true;
      }

      @Override
      public String toString() {
         return String.format("%s%s", getClass().getSimpleName(), m_parts);
      }
   }
}
