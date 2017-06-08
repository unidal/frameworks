package org.unidal.lookup.container.model;

import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public interface IVisitor {

   public void visitAny(Any any);

   public void visitComponent(ComponentModel component);

   public void visitConfiguration(ConfigurationModel configuration);

   public void visitPlexus(PlexusModel plexus);

   public void visitRequirement(RequirementModel requirement);
}
