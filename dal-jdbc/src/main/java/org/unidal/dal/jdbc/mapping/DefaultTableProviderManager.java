package org.unidal.dal.jdbc.mapping;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;

@Named(type = TableProviderManager.class)
public class DefaultTableProviderManager extends ContainerHolder implements TableProviderManager {
   private Map<String, TableProvider> m_tableProviders = new HashMap<String, TableProvider>();

   public TableProvider getTableProvider(String logicalName) {
      TableProvider tableProvider = m_tableProviders.get(logicalName);

      if (tableProvider == null) {
         synchronized (m_tableProviders) {
            tableProvider = m_tableProviders.get(logicalName);

            if (tableProvider == null) {
               tableProvider = lookup(TableProvider.class, logicalName);
               m_tableProviders.put(logicalName, tableProvider);
            }
         }
      }

      return tableProvider;
   }
}
