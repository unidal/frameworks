package org.unidal.concurrent.internals;

import java.util.HashMap;
import java.util.Map;

import org.unidal.concurrent.Stage;
import org.unidal.concurrent.StageManager;
import org.unidal.helper.Threads;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;

@Named(type = StageManager.class)
public class DefaultStageManager extends ContainerHolder implements StageManager {
   private Map<String, Stage<?>> m_stages = new HashMap<String, Stage<?>>();

   @Override
   @SuppressWarnings("unchecked")
   public <E> Stage<E> getStage(String id) {
      Stage<?> stage = m_stages.get(id);

      if (stage == null) {
         stage = lookup(Stage.class);

         if (stage instanceof DefaultStage) {
            ((DefaultStage<E>) stage).setId(id);
         }

         Threads.forGroup("Cat").start(stage);
         m_stages.put(id, stage);
      }

      return (Stage<E>) stage;
   }
}
