package org.unidal.lookup.container.model.transform;

import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public interface IParser<T> {
   public PlexusModel parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForComponentModel(IMaker<T> maker, ILinker linker, ComponentModel parent, T node);

   public void parseForConfigurationModel(IMaker<T> maker, ILinker linker, ConfigurationModel parent, T node);

   public void parseForRequirementModel(IMaker<T> maker, ILinker linker, RequirementModel parent, T node);
}
