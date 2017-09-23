package org.unidal.dal.jdbc.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.datasource.model.entity.DataSourceDef;
import org.unidal.dal.jdbc.datasource.model.entity.DataSourcesDef;
import org.unidal.dal.jdbc.datasource.model.entity.PropertiesDef;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;

@Named
public class JdbcDataSourceDescriptorManager extends ContainerHolder implements Initializable {
   private Map<String, JdbcDataSourceDescriptor> m_descriptors = new HashMap<String, JdbcDataSourceDescriptor>();

   private List<DataSourceProvider> m_providers;

   private String m_datasourceFile;

   protected JdbcDataSourceDescriptor buildDescriptor(DataSourceDef ds) {
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
      d.setProperty("checkout-timeout", ds.getCheckoutTimeoutInMillis());

      return d;
   }

   private DataSourceDef findDataSource(String id) {
      for (DataSourceProvider provider : m_providers) {
         DataSourcesDef def = provider.defineDatasources();

         if (def != null) {
            DataSourceDef ds = def.findDataSource(id);

            if (ds != null) {
               return ds;
            }
         }
      }

      return null;
   }

   public List<String> getDataSourceNames() {
      List<String> names = new ArrayList<String>();

      for (DataSourceProvider provider : m_providers) {
         DataSourcesDef def = provider.defineDatasources();

         if (def != null) {
            for (String name : def.getDataSourcesMap().keySet()) {
               if (!names.contains(name)) {
                  names.add(name);
               }
            }
         }
      }

      return names;
   }

   public JdbcDataSourceDescriptor getDescriptor(String id) {
      JdbcDataSourceDescriptor configuration = m_descriptors.get(id);

      if (configuration == null) {
         DataSourceDef ds = findDataSource(id);

         if (ds != null) {
            configuration = buildDescriptor(ds);
            m_descriptors.put(id, configuration);
         }
      }

      return configuration;
   }

   public void initialize() throws InitializationException {
      m_providers = new ArrayList<DataSourceProvider>(lookupList(DataSourceProvider.class));

      if (m_providers.isEmpty()) {
         throw new InitializationException("No DataSourceProvider found!");
      }

      // for back compatible to old component definition
      if (m_datasourceFile != null) {
         for (DataSourceProvider provider : m_providers) {
            if (provider instanceof DefaultDataSourceProvider) {
               DefaultDataSourceProvider p = (DefaultDataSourceProvider) provider;

               if (p.getDatasourceFile() == null) {
                  p.setDatasourceFile(m_datasourceFile);
               }
            }
         }
      }
   }

   public void setDatasourceFile(String datasourceFile) {
      m_datasourceFile = datasourceFile;
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
