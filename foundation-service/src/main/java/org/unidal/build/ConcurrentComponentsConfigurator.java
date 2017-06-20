package org.unidal.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.concurrent.internals.DefaultActorManager;
import org.unidal.concurrent.internals.DefaultStage;
import org.unidal.concurrent.internals.DefaultStageManager;
import org.unidal.concurrent.internals.DefaultThreadPool;
import org.unidal.concurrent.internals.DefaultThreadPoolManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

class ConcurrentComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultStageManager.class));
      all.add(A(DefaultStage.class));
      all.add(A(DefaultActorManager.class));
      all.add(A(DefaultThreadPool.class));
      all.add(A(DefaultThreadPoolManager.class));

      return all;
   }
}
