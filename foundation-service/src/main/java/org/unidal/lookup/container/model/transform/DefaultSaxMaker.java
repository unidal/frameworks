package org.unidal.lookup.container.model.transform;

import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;
import org.xml.sax.Attributes;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public Any buildAny(Attributes attributes) {
      throw new UnsupportedOperationException("Not needed!");
   }

   @Override
   public ComponentModel buildComponent(Attributes attributes) {
      ComponentModel component = new ComponentModel();

      return component;
   }

   @Override
   public ConfigurationModel buildConfiguration(Attributes attributes) {
      ConfigurationModel configuration = new ConfigurationModel();

      return configuration;
   }

   @Override
   public PlexusModel buildPlexus(Attributes attributes) {
      PlexusModel plexus = new PlexusModel();

      return plexus;
   }

   @Override
   public RequirementModel buildRequirement(Attributes attributes) {
      RequirementModel requirement = new RequirementModel();

      return requirement;
   }
}
