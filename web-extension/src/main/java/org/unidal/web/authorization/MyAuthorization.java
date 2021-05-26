package org.unidal.web.authorization;

import static org.unidal.web.config.ConfigService.CATEGORY_SECURITY;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.unidal.cat.Cat;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.web.config.ConfigEvent;
import org.unidal.web.config.ConfigEventListener;
import org.unidal.web.config.ConfigException;
import org.unidal.web.config.ConfigService;
import org.unidal.web.security.authorization.entity.ApplicationModel;
import org.unidal.web.security.authorization.entity.AuthorizationModel;
import org.unidal.web.security.authorization.entity.RoleDef;
import org.unidal.web.security.authorization.entity.UserModel;
import org.unidal.web.security.authorization.transform.BaseVisitor;
import org.unidal.web.security.authorization.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

@Named
public class MyAuthorization implements Initializable {
   private static final String AUTHORIZATION_XML = "authorization.xml";

   @Inject
   private ConfigService m_configService;

   protected AuthorizationModel m_authorization;

   public Set<String> findPermissionsForApp(String appId) {
      ApplicationModel app = m_authorization.findApplication(appId);

      if (app == null) {
         app = m_authorization.findApplication("*"); // all actors supported
      }

      if (app != null && app.isEnabled()) {
         return app.getPermissions();
      } else {
         return Collections.emptySet();
      }
   }

   public Set<String> findPermissionsForUser(String username) {
      UserModel user = m_authorization.findUser(username);

      if (user != null && user.isEnabled()) {
         return user.getPermissions();
      } else {
         return Collections.emptySet();
      }
   }

   protected Set<String> findRoles(String appId) { // NOT used right now
      ApplicationModel app = m_authorization.findApplication(appId);

      if (app == null) {
         app = m_authorization.findApplication("*"); // all actors supported
      }

      if (app != null && app.isEnabled()) {
         return app.getRoles();
      } else {
         return Collections.emptySet();
      }
   }

   @Override
   public void initialize() throws InitializationException {
      m_configService.register(new ConfigChangeListener());

      try {
         m_authorization = loadAuthorization();
      } catch (ConfigException e) {
         Cat.logError(e);

         throw new InitializationException("Error when loading " + AUTHORIZATION_XML + " from MySQL!", e);
      } catch (Exception e) {
         Cat.logEvent("Tulip.BadConfig", ConfigService.CATEGORY_SECURITY + ":" + AUTHORIZATION_XML);
         Cat.logError(e);
      }

      if (m_authorization == null) {
         m_authorization = new AuthorizationModel();
      }
   }

   private AuthorizationModel loadAuthorization() throws ConfigException, SAXException, IOException {
      String xml = m_configService.getString(ConfigService.CATEGORY_SECURITY, AUTHORIZATION_XML, null);

      if (xml != null) {
         AuthorizationModel authorization = DefaultSaxParser.parse(xml);

         authorization.accept(new Verification());
         return authorization;
      } else {
         return null;
      }
   }

   private class ConfigChangeListener implements ConfigEventListener {
      @Override
      public void onEvent(ConfigEvent event) throws ConfigException {
         if (event.isEligible(CATEGORY_SECURITY, AUTHORIZATION_XML)) {
            try {
               AuthorizationModel authorization = loadAuthorization();

               if (authorization != null) {
                  m_authorization = authorization;
               }
            } catch (ConfigException e) {
               Cat.logError(e);
            } catch (Exception e) {
               Cat.logError(e);
            }
         }
      }
   }

   public static class Verification extends BaseVisitor {
      private AuthorizationModel m_authorization;

      @Override
      public void visitApplication(ApplicationModel application) {
         // prepare all permissions
         if (application.isEnabled()) {
            Set<String> permissions = new HashSet<String>();

            for (String role : application.getRoles()) {
               RoleDef def = m_authorization.findRoleDef(role);

               if (def == null) {
                  throw new RuntimeException(String.format("No role(%s) defined!", role));
               } else if (def.isEnabled()) {
                  permissions.addAll(def.getPermissions());
               }
            }

            application.setPermissions(permissions);
         }
      }

      @Override
      public void visitAuthorization(AuthorizationModel authorization) {
         m_authorization = authorization;
         super.visitAuthorization(authorization);
      }

      @Override
      public void visitUser(UserModel user) {
         // prepare all permissions
         if (user.isEnabled()) {
            Set<String> permissions = new HashSet<String>();

            for (String role : user.getRoles()) {
               RoleDef def = m_authorization.findRoleDef(role);

               if (def == null) {
                  throw new RuntimeException(String.format("No role(%s) defined!", role));
               } else if (def.isEnabled()) {
                  permissions.addAll(def.getPermissions());
               }
            }

            user.setPermissions(permissions);
         }
      }
   }
}
