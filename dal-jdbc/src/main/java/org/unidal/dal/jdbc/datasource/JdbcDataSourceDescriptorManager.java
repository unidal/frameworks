package org.unidal.dal.jdbc.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourceDef;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.entity.PropertiesDef;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;

@Named
public class JdbcDataSourceDescriptorManager extends ContainerHolder implements Initializable {
   private Map<String, JdbcDataSourceDescriptor> m_descriptors = new HashMap<String, JdbcDataSourceDescriptor>();

   private DataSourcesDef m_dataSources;

   public List<String> getDataSourceNames() {
      List<String> names = new ArrayList<String>();

      for (DataSourceDef ds : m_dataSources.getDataSourcesMap().values()) {
         names.add(ds.getId());
      }

      return names;
   }

   protected JdbcDataSourceDescriptor getDescriptor(DataSourceDef ds) {
      JdbcDataSourceDescriptor d = new JdbcDataSourceDescriptor();
      PropertiesDef properties = ds.getProperties();
      String url = properties.getUrl();
      String connectionProperties = properties.getConnectionProperties();

      if (connectionProperties != null && connectionProperties.length() > 0) {
         d.setProperty("url", url + "?" + connectionProperties);
      } else {
         d.setProperty("url", url);
      }

      d.setId(ds.getId());
      d.setType(ds.getType());
      d.setProperty("driver", properties.getDriver());
      d.setProperty("user", properties.getUser());
      d.setProperty("password", properties.getPassword());
      d.setProperty("login-timeout", toTime(ds.getConnectionTimeout()));
      d.setProperty("max-idle-time", toTime(ds.getIdleTimeout()));
      d.setProperty("min-pool-size", ds.getMinimumPoolSize());
      d.setProperty("max-pool-size", ds.getMaximumPoolSize());

      return d;
   }

   public JdbcDataSourceDescriptor getDescriptor(String id) {
      JdbcDataSourceDescriptor configuration = m_descriptors.get(id);

      if (configuration == null) {
         if (id != null) {
            DataSourceDef ds = m_dataSources.findDataSource(id);

            if (ds != null) {
               configuration = getDescriptor(ds);
               m_descriptors.put(id, configuration);
            }
         }
      }

      return configuration;
   }

   public void initialize() throws InitializationException {
      DataSourcesDef dataSources = new DataSourcesDef();

      try {
         List<DataSourceProvider> providers = lookupList(DataSourceProvider.class);
         int size = providers.size();

         for (int i = size - 1; i >= 0; i--) {
            DataSourceProvider provider = providers.get(i);
            DataSourcesDef def = provider.defineDatasources();

            for (DataSourceDef dataSource : def.getDataSourcesMap().values()) {
               dataSources.addDataSource(dataSource);
            }
         }
      } catch (RuntimeException e) {
         throw new InitializationException(e.getMessage(), e);
      }

      if (dataSources.getDataSourcesMap().isEmpty()) {
         throw new InitializationException("No datasources defined!");
      }

      m_dataSources = dataSources;
   }

   protected int toTime(String source) {
      int time = 0;
      int len = source == null ? 0 : source.length();

      int num = 0;
      for (int i = 0; i < len; i++) {
         char ch = source.charAt(i);

         switch (ch) {
         case 'd':
            time += num * 24 * 60 * 60;
            num = 0;
            break;
         case 'h':
            time += num * 60 * 60;
            num = 0;
            break;
         case 'm':
            time += num * 60;
            num = 0;
            break;
         case 's':
            time += num;
            num = 0;
            break;
         default:
            if (ch >= '0' && ch <= '9') {
               num = num * 10 + (ch - '0');
            } else {
               throw new IllegalArgumentException("Invalid character found: " + ch + ", should be one of [0-9][dhms]");
            }
         }
      }

      return time;
   }
}
