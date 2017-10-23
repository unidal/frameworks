package org.unidal.dal.jdbc;

import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;

public abstract class AbstractDao extends ContainerHolder implements Initializable {
   @Inject
   private QueryEngine m_queryEngine;

   protected QueryEngine getQueryEngine() {
      return m_queryEngine;
   }

   protected abstract Class<?>[] getEntityClasses();

   public void initialize() throws InitializationException {
      m_queryEngine = lookup(QueryEngine.class);

      // register relevant entity class
      EntityInfoManager entityInfoManager = lookup(EntityInfoManager.class);

      for (Class<?> entityClass : getEntityClasses()) {
         entityInfoManager.register(entityClass);
      }
   }
}
