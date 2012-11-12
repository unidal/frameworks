package org.unidal.eunit.invocation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.spi.ICaseContext;

public class EunitMethodInvoker implements IMethodInvoker {
   @SuppressWarnings("unchecked")
   @Override
   public <T> T invoke(ICaseContext ctx, EunitMethod eunitMethod) throws Throwable {
      Object testInstance = ctx.getTestInstance();
      List<EunitParameter> params = eunitMethod.getParameters();
      Object[] args = new Object[params.size()];

      resolveArguments(ctx, params, args);

      try {
         return (T) eunitMethod.getMethod().invoke(testInstance, args);
      } catch (InvocationTargetException e) {
         throw e.getCause();
      }
   }

   protected void resolveArguments(ICaseContext ctx, List<EunitParameter> params, Object[] args) {
      int len = params.size();

      for (int i = 0; i < len; i++) {
         EunitParameter parameter = params.get(i);
         Object value = ctx.findAttributeFor(parameter);

         args[i] = value;
      }
   }
}
