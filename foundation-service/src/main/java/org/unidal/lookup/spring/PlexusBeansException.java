package org.unidal.lookup.spring;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.springframework.beans.BeansException;

public class PlexusBeansException extends BeansException {
   private static final long serialVersionUID = 1L;

   public PlexusBeansException(String message, ComponentLookupException cause) {
      super(message, cause);
   }
}
