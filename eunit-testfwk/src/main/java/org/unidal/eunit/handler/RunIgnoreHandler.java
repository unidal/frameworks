package org.unidal.eunit.handler;

import org.unidal.eunit.annotation.RunIgnore;
import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.filter.RunOption;

public enum RunIgnoreHandler implements IAnnotationHandler<RunIgnore, Class<?>> {
   INSTANCE;

   @Override
   public Class<RunIgnore> getTargetAnnotation() {
      return RunIgnore.class;
   }

   @Override
   public void handle(IClassContext context, RunIgnore meta, Class<?> clazz) {
      EunitClass eunitClass = context.forEunit().peek();
      RunOption option = meta.runAll() ? RunOption.ALL_CASES : RunOption.IGNORED_CASES_ONLY;

      switch (option) {
      case IGNORED_CASES_ONLY:
      case ALL_CASES:
         if (eunitClass.isIgnored()) {
            eunitClass.setIgnored(false);
         }

         for (EunitMethod eunitMethod : eunitClass.getMethods()) {
            if (eunitMethod.isTest()) {
               if (eunitMethod.isIgnored()) {
                  eunitMethod.setIgnored(false);
               } else if (option == RunOption.IGNORED_CASES_ONLY) {
                  eunitMethod.setIgnored(true);
               }
            }
         }

         break;
      default:
         break;
      }
   }

   @Override
   public boolean isAfter() {
      return true;
   }

   @Override
   public String toString() {
      return String.format("%s.%s", getClass().getSimpleName(), name());
   }
}
