package org.unidal.net;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.unidal.lookup.ContainerLoader;

public class Transports {
   public static ServerTransport asServer() {
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      try {
         ServerTransport transport = container.lookup(ServerTransport.class);

         return transport;
      } catch (ComponentLookupException e) {
         throw new IllegalStateException(String.format("Unable to lookup component(%s)!",
               ServerTransport.class.getSimpleName()), e);
      }
   }

   public static ClientTransport asClient() {
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      try {
         ClientTransport transport = container.lookup(ClientTransport.class);

         return transport;
      } catch (ComponentLookupException e) {
         throw new IllegalStateException(String.format("Unable to lookup component(%s)!",
               ClientTransport.class.getSimpleName()), e);
      }
   }
}
