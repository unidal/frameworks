package org.unidal.web.authorization;

import static org.unidal.web.config.ConfigService.CATEGORY_SECURITY;

import java.io.IOException;
import java.util.List;

import org.unidal.cat.Cat;
import org.unidal.helper.Splitters;
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
import org.unidal.web.security.authorization.transform.BaseVisitor;
import org.unidal.web.security.authorization.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

@Named
public class MyApplication implements Initializable {
   private static final String MAPPING_APPID_IP = "mapping.appid.ip";

   private static final String AUTHORIZATION_XML = "authorization.xml";

   @Inject
   private ConfigService m_configService;
//
//   @Inject
//   private ModelManager m_modelManager;

   protected AuthorizationModel m_authorization;

   private boolean m_clientIpEnabled;

   private String m_clietIpEntityName;

   private String m_clientIpParamField;

   private String m_clientIpResultField;

   @Override
   public void initialize() throws InitializationException {
      m_configService.register(new ConfigChangeListener());

      try {
         loadClientIpParameters();
      } catch (ConfigException e) {
         Cat.logError(e);

         throw new InitializationException("Error when loading " + MAPPING_APPID_IP + " from MySQL!", e);
      }

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

   public boolean isMatched(String appId, String host) {
      ApplicationModel actor = m_authorization.findApplication(appId);

      if (actor == null) {
         actor = m_authorization.findApplication("*"); // all actors supported
      }

      if (actor != null && actor.isEnabled()) {
         if (actor.getHosts().contains(host)) {
            return true;
         } else if (actor.getHosts().contains("*")) { // all hosts supported
            return true;
         }
      }

      return false;
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

   private void loadClientIpParameters() throws ConfigException {
      String value = m_configService.getString(ConfigService.CATEGORY_SECURITY, MAPPING_APPID_IP, null);

      if (value == null || value.length() == 0) {
         Cat.logEvent("Tulip.NoConfig", ConfigService.CATEGORY_SECURITY + ":" + MAPPING_APPID_IP);
         value = "node:appid:ip";
      }

      List<String> parts = Splitters.by(':').trim().split(value);

      if (parts.size() == 3) {
         m_clientIpEnabled = true;
         m_clietIpEntityName = parts.get(0);
         m_clientIpParamField = parts.get(1);
         m_clientIpResultField = parts.get(2);
      } else {
         Cat.logEvent("Tulip.BadConfig", ConfigService.CATEGORY_SECURITY + ":" + MAPPING_APPID_IP, value);
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

         if (event.isEligible(CATEGORY_SECURITY, MAPPING_APPID_IP)) {
            try {
               loadClientIpParameters();
            } catch (ConfigException e) {
               Cat.logError(e);
            }
         }
      }
   }

   public class Verification extends BaseVisitor {
      @Override
      public void visitAuthorization(AuthorizationModel authorization) {
         // prepare all hosts
         if (m_clientIpEnabled) {
//            EntityGroup group = m_modelManager.findEntityGroup(m_clietIpEntityName);
//
//            if (group != null) {
//               for (Any entity : group.getEntities()) {
//                  String appId = entity.getAttribute(m_clientIpParamField);
//                  ApplicationModel actor = authorization.findApplication(appId);
//
//                  if (actor != null && actor.isEnabled()) {
//                     String ip = entity.getAttribute(m_clientIpResultField);
//
//                     if (ip != null) {
//                        actor.addHost(ip);
//                     }
//                  }
//               }
//            }
         }
      }
   }
}
