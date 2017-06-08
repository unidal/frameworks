package org.unidal.lookup.container.model.transform;

import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public interface IMaker<T> {

   public Any buildAny(T node);

   public ComponentModel buildComponent(T node);

   public ConfigurationModel buildConfiguration(T node);

   public PlexusModel buildPlexus(T node);

   public RequirementModel buildRequirement(T node);
}
