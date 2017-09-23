package org.unidal.lookup.container.lifecycle;

import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.logging.Logger;

public interface LifecycleContext {
   public Object getComponent();

   public ComponentModel getComponentModel();

   public PlexusContainer getContainer();

   public Logger getLogger(String role);

   public Object lookup(String role, String roleHint) throws ComponentLookupException;
}
