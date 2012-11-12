package org.unidal.eunit.handler;

import org.unidal.eunit.annotation.RunGroups;
import org.unidal.eunit.testfwk.EunitRuntimeConfig;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.filter.GroupFilter;

public enum RunGroupsHandler implements IAnnotationHandler<RunGroups, Class<?>> {
   INSTANCE;

   @Override
   public Class<RunGroups> getTargetAnnotation() {
      return RunGroups.class;
   }

   @Override
   /**
    * <ul>
    * following group filters to be considered:
    * <li>-Dgroups="P1 P2 -P3 -P4"</li>
    * <li>-groups P1 P2 '-P3' '-P4' in -DargFile=...</li>
    * <li>@RunGroups(include = { "P1", "P2" }, exclude = { "P3", "P4" })</li>
    * <li>Not specified</li>
    * </ul>
    */
   public void handle(IClassContext ctx, RunGroups meta, Class<?> target) {
      if (EunitRuntimeConfig.INSTANCE.getGroupFilter() == null) {
         GroupFilter filter = new GroupFilter(meta.include(), meta.exclude());

         EunitRuntimeConfig.INSTANCE.setGroupFilter(filter);
      }
   }

   @Override
   public boolean isAfter() {
      return false;
   }

   @Override
   public String toString() {
      return String.format("%s.%s", getClass().getSimpleName(), name());
   }
}
