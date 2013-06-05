package org.unidal.lookup.extension;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class PostConstructionPhase implements Phase {
   @Override
   @SuppressWarnings("rawtypes")
   public void execute(Object component, ComponentManager manager, ClassRealm realm) throws PhaseExecutionException {
      if (component instanceof RoleHintEnabled) {
         ((RoleHintEnabled) component).enableRoleHint(manager.getRoleHint());
      }
   }
}