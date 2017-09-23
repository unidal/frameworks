package org.unidal.lookup.container.lifecycle;

import org.unidal.lookup.ComponentLookupException;

public interface LifecycleHandler {
   public void handleStart(LifecycleContext ctx) throws ComponentLookupException;

   public void handleStop(LifecycleContext ctx);
}
