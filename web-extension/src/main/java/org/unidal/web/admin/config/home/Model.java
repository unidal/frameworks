package org.unidal.web.admin.config.home;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.config.Config;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<ConfigPage, Action, Context> {
   private List<String> m_categories;

   private String m_category;

   private List<Config> m_configs;

   private Config m_config;

   public Model(Context ctx) {
      super(ctx);
   }

   public List<String> getCategories() {
      return m_categories;
   }

   public String getCategory() {
      return m_category;
   }

   public Config getConfig() {
      return m_config;
   }

   public ConfigBean getConfigBean() {
      return new ConfigBean(m_config);
   }

   public List<Config> getConfigs() {
      return m_configs;
   }

   public List<ConfigBean> getConfigBeans() {
      List<ConfigBean> beans = new ArrayList<ConfigBean>();

      for (Config config : m_configs) {
         beans.add(new ConfigBean(config));
      }

      return beans;
   }

   @Override
   public Action getDefaultAction() {
      return Action.LIST;
   }

   public void setCategories(List<String> categories) {
      m_categories = categories;
   }

   public void setCategory(String category) {
      m_category = category;
   }

   public void setConfig(Config config) {
      m_config = config;
   }

   public void setConfigs(List<Config> configs) {
      m_configs = configs;
   }

   public static class ConfigBean {
      private Config m_config;

      public ConfigBean(Config config) {
         m_config = config;
      }

      public String getName() {
         return m_config.getName();
      }

      public String getCategory() {
         return m_config.getCategory();
      }

      public String getDescription() {
         return m_config.getDescription();
      }

      public String getDetails() {
         try {
            return new String(m_config.getDetails(), "utf-8");
         } catch (UnsupportedEncodingException e) {
            return new String(m_config.getDetails());
         }
      }
   }
}
