package org.unidal.web.config;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.unidal.cat.Cat;
import org.unidal.cat.message.Transaction;
import org.unidal.dal.jdbc.DalException;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.helper.Urls;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.web.admin.dal.config.ConfigDao;
import org.unidal.web.admin.dal.config.ConfigDo;
import org.unidal.web.admin.dal.config.ConfigEntity;

@Named(type = ConfigService.class)
public class DefaultConfigService implements ConfigService, Initializable, LogEnabled {
   @Inject
   private ConfigDao m_configDao;

   private ConcurrentMap<CacheKey, String> m_cached = new ConcurrentHashMap<CacheKey, String>();

   private List<ConfigEventListener> m_listeners = new ArrayList<ConfigEventListener>();

   private ConfigEventDispatcher m_dispatcher = new ConfigEventDispatcher();

   private Logger m_logger;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   @Override
   public List<String> findCategories() throws ConfigException {
      List<String> categories = new ArrayList<String>();

      try {
         List<ConfigDo> configs = m_configDao.findAll(ConfigEntity.READSET_CATEGORY);

         for (ConfigDo config : configs) {
            String category = config.getCategory();

            if (!categories.contains(category)) {
               categories.add(category);
            }
         }
      } catch (DalException e) {
         Cat.logError(e);
      }

      Collections.sort(categories);
      return categories;
   }

   @Override
   public Config findConfig(String category, String name) throws ConfigException {
      try {
         ConfigDo config = m_configDao.findByCategoryAndName(category, name, ConfigEntity.READSET_FULL);

         return new Config(config);
      } catch (DalException e) {
         Cat.logError(e);
      }

      return null;
   }

   @Override
   public List<Config> findConfigs(String category) throws ConfigException {
      List<Config> configs = new ArrayList<Config>();

      try {
         List<ConfigDo> list = m_configDao.findAllByCategory(category, ConfigEntity.READSET_FULL);

         for (ConfigDo item : list) {
            configs.add(new Config(item));
         }
      } catch (DalException e) {
         throw new ConfigException("Error when updating configs to MySQL!" + e, e);
      }

      return configs;
   }

   @Override
   public boolean getBoolean(String category, String name, boolean defaultValue) {
      String value = getString(category, name, null);

      if (value == null) {
         return defaultValue;
      } else {
         return Boolean.parseBoolean(value);
      }
   }

   public String getString(String category, String name, String defaultValue) {
      CacheKey key = new CacheKey(category, name);
      String value = m_cached.get(key);

      if (value == null) {
         try {
            ConfigDo config = m_configDao.findByCategoryAndName(category, name, ConfigEntity.READSET_FULL);

            value = toString(config.getDetails());
            m_cached.put(key, value);
         } catch (DalException e) {
            // ignore it
            Cat.logError(e);
         }
      }

      if (value != null) {
         return value;
      } else {
         return defaultValue;
      }
   }

   @Override
   public void initialize() throws InitializationException {
      Threads.forGroup().start(new CacheRefreshTask());
   }

   @Override
   public int refreshCache() {
      List<CacheKey> keys = new ArrayList<CacheKey>(m_cached.keySet());
      Transaction t = Cat.newTransaction("Config", "Cache.Refresh");
      int count = 0;

      try {
         for (CacheKey key : keys) {
            try {
               String category = key.getCategory();
               String name = key.getName();
               ConfigDo config = m_configDao.findByCategoryAndName(category, name, ConfigEntity.READSET_FULL);
               String newValue = toString(config.getDetails());
               String oldValue = m_cached.put(key, newValue);

               if (!newValue.equals(oldValue)) {
                  ConfigEvent event = new ConfigEvent(new Config(config), true);

                  m_dispatcher.dispatch(event);
                  count++;
               }
            } catch (Exception e) {
               Cat.logError(e);
            }
         }

         t.success();
      } catch (Throwable e) {
         t.setStatus(e);
      } finally {
         t.complete();
      }

      return count;
   }

   public void register(ConfigEventListener listener) {
      if (!m_listeners.contains(listener)) {
         m_listeners.add(listener);
      }
   }

   private byte[] toBytes(String str) {
      try {
         return str.getBytes("utf-8");
      } catch (UnsupportedEncodingException e) {
         return str.getBytes();
      }
   }

   private String toString(byte[] bytes) {
      try {
         return new String(bytes, "utf-8");
      } catch (UnsupportedEncodingException e) {
         return new String(bytes);
      }
   }

   @Override
   public void updateConfig(String category, String name, String description, String value) throws ConfigException {
      ConfigDo c = new ConfigDo();

      c.setCategory(category);
      c.setName(name);
      c.setDescription(description);
      c.setDetails(toBytes(value));
      c.setStatus(1);

      try {
         m_configDao.upsert(c); // update or insert

         CacheKey key = new CacheKey(category, name);
         ConfigEvent event = new ConfigEvent(new Config(c));
         String oldValue = m_cached.put(key, value);

         if (!value.equals(oldValue)) {
            m_dispatcher.dispatch(event);
         }
      } catch (DalException e) {
         throw new ConfigException("Error when updating config to MySQL!" + e, e);
      }
   }

   private static class CacheKey {
      private String m_category;

      private String m_name;

      public CacheKey(String category, String name) {
         m_category = category;
         m_name = name;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CacheKey) {
            CacheKey key = (CacheKey) obj;

            if (!key.m_category.equals(m_category)) {
               return false;
            }

            if (!key.m_name.equals(m_name)) {
               return false;
            }

            return true;
         }

         return false;
      }

      public String getCategory() {
         return m_category;
      }

      public String getName() {
         return m_name;
      }

      @Override
      public int hashCode() {
         int hash = 0;

         hash = hash * 31 + m_category.hashCode();
         hash = hash * 31 + m_name.hashCode();
         return hash;
      }

      @Override
      public String toString() {
         return m_category + ":" + m_name;
      }
   }

   private class CacheRefreshTask implements Task {
      private static final long ONE_MINUTE = 60 * 1000L;

      private long m_lastRefreshTime;

      private AtomicBoolean m_enabled = new AtomicBoolean(true);

      private CountDownLatch m_latch = new CountDownLatch(1);

      @Override
      public String getName() {
         return getClass().getSimpleName();
      }

      @Override
      public void run() {
         try {
            while (m_enabled.get()) {
               long now = System.currentTimeMillis();

               if (now - m_lastRefreshTime >= 5 * ONE_MINUTE) {
                  int count = refreshCache();

                  if (count > 0) {
                     m_logger.info(String.format("%s cache entries refreshed.", count));
                  }

                  m_lastRefreshTime = now;
               }

               TimeUnit.MILLISECONDS.sleep(100); // 100 ms
            }
         } catch (InterruptedException e) {
            // ignore it
         } catch (Exception e) {
            Cat.logError(e);
         } finally {
            m_latch.countDown();
         }
      }

      @Override
      public void shutdown() {
         m_enabled.set(false);

         try {
            m_latch.await();
         } catch (InterruptedException e) {
            // ignore it
         }
      }
   }

   private class ConfigEventDispatcher {
      public void dispatch(ConfigEvent event) {
         List<ConfigEventListener> listeners = new ArrayList<ConfigEventListener>(m_listeners);

         // make sure it in the last since it takes time to complete
         if (!event.isLocalOnly()) {
            listeners.add(new RemoteCacheRefreshListener());
         }

         for (ConfigEventListener listener : listeners) {
            try {
               listener.onEvent(event);
            } catch (Exception e) {
               Cat.logError(e);
            }
         }
      }
   }

   private class RemoteCacheRefreshListener implements ConfigEventListener {
      @Override
      public void onEvent(ConfigEvent event) {
         try {
            String pattern = getString(CATEGORY_CONFIG, "cluster.server-uri.pattern", "http://%s/").trim();
            String endpoints = getString(CATEGORY_CONFIG, "cluster.endpoints", "");
            List<String> list = Splitters.by(',').trim().noEmptyItem().split(endpoints);

            if (!pattern.endsWith("/")) {
               pattern = pattern + "/";
            }

            if (list.isEmpty()) {
               m_logger.warn("No cluster endpoints configured, run in single node mode.");
            } else {
               for (String endpoint : list) {
                  Transaction t = Cat.newTransaction("Config", "Cache.Refresh:" + endpoint);

                  try {
                     String url = String.format(pattern + "config/refresh", endpoint);

                     t.addData(url);

                     InputStream in = Urls.forIO().connectTimeout(1000).readTimeout(1000).openStream(url);

                     Files.forIO().readFrom(in, "utf-8");
                     t.success();
                  } catch (Throwable e) {
                     t.setStatus(e);
                  } finally {
                     t.complete();
                  }
               }
            }
         } catch (Exception e) {
            Cat.logError(e);
         }
      }
   }
}
