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

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class || type == Boolean.TYPE) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class || type == Integer.TYPE) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class || type == Long.TYPE) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class || type == Short.TYPE) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class || type == Float.TYPE) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class || type == Double.TYPE) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class || type == Byte.TYPE) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class || type == Character.TYPE) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }
}
