package org.unidal.lookup.container.model.transform;

import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public interface ILinker {

   public boolean onComponent(PlexusModel parent, ComponentModel component);

   public boolean onConfiguration(ComponentModel parent, ConfigurationModel configuration);

   public boolean onRequirement(ComponentModel parent, RequirementModel requirement);
}
