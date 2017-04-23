package org.unidal.lookup.container.lifecycle;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface LifecycleHandler {
   public void handleStart(LifecycleContext ctx) throws ComponentLookupException;

   public void handleStop(LifecycleContext ctx);
}
