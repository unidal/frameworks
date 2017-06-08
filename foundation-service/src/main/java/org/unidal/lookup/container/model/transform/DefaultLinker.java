package org.unidal.lookup.container.model.transform;

import java.util.ArrayList;
import java.util.List;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public class DefaultLinker implements ILinker {
   @SuppressWarnings("unused")
   private boolean m_deferrable;

   private List<Runnable> m_deferedJobs = new ArrayList<Runnable>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }

   @Override
   public boolean onComponent(final PlexusModel parent, final ComponentModel component) {
      parent.addComponent(component);
      return true;
   }

   @Override
   public boolean onConfiguration(final ComponentModel parent, final ConfigurationModel configuration) {
      parent.setConfiguration(configuration);
      return true;
   }

   @Override
   public boolean onRequirement(final ComponentModel parent, final RequirementModel requirement) {
      parent.addRequirement(requirement);
      return true;
   }
}
