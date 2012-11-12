package org.unidal.eunit.invocation;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.CaseContext;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IResourceBase;

public enum EunitParameterResolver implements IParameterResolver<CaseContext> {
   INSTANCE;

   @Override
   public boolean matches(final CaseContext ctx, final EunitParameter parameter) {
      final Class<?> type = parameter.getType();

      return type == EunitClass.class || type == EunitMethod.class || type == ICaseContext.class
            || type == IClassContext.class || type == IResourceBase.class;
   }

   @Override
   public Object resolve(final CaseContext ctx, final EunitParameter parameter) {
      final Class<?> type = parameter.getType();

      if (type == EunitClass.class) {
         return ctx.getEunitClass();
      } else if (type == EunitMethod.class) {
         return ctx.getEunitMethod();
      } else if (type == ICaseContext.class) {
         return ctx;
      } else if (type == IClassContext.class) {
         return ctx.getClassContext();
      } else if (type == IResourceBase.class) {
         return ctx.getEunitClass();
      }

      return null;
   }
}
