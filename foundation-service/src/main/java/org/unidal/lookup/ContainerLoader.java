package org.unidal.lookup;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

public class ContainerLoader {
	private static volatile PlexusContainer s_container;

	public static PlexusContainer getDefaultContainer() {
		ContainerConfiguration configuration = new DefaultContainerConfiguration();

		configuration.setContainerConfiguration("/META-INF/plexus/plexus.xml");
		return getDefaultContainer(configuration);
	}

	public static PlexusContainer getDefaultContainer(ContainerConfiguration configuration) {
		if (s_container == null) {
			synchronized (ContainerLoader.class) {
				if (s_container == null) {
					try {
						s_container = new DefaultPlexusContainer(configuration);
					} catch (PlexusContainerException e) {
						throw new RuntimeException("Unable to create Plexus container.", e);
					}
				}
			}
		}

		return s_container;
	}
}
