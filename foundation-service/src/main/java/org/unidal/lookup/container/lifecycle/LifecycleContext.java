package org.unidal.lookup.container.lifecycle;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.container.model.entity.ComponentModel;

public interface LifecycleContext {
   public Object getComponent();

   public ComponentModel getComponentModel();

   public PlexusContainer getContainer();

   public Logger getLogger(String role);

   public Object lookup(String role, String roleHint) throws ComponentLookupException;
}
