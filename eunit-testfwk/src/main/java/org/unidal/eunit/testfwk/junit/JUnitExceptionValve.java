package org.unidal.eunit.testfwk.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.unidal.eunit.model.entity.EunitException;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.IValveChain;

public enum JUnitExceptionValve implements IValve<ICaseContext> {
   INSTANCE;

   @Override
   public void execute(ICaseContext ctx, IValveChain chain) throws Throwable {
      EunitMethod eunitMethod = ctx.getEunitMethod();
      List<EunitException> expectedExceptions = eunitMethod.getExpectedExceptions();

      if (expectedExceptions.isEmpty()) {
         chain.executeNext(ctx);
      } else {
         try {
            chain.executeNext(ctx);

            throw new AssertionError(buildMessage(expectedExceptions, null));
         } catch (Throwable e) {
            // TODO for exception match, message and pattern
            
            if (e instanceof InvocationTargetException) {
               e = ((InvocationTargetException)e).getCause();
            }
            for (EunitException expectedException : expectedExceptions) {
               Class<?> type = expectedException.getType();

               if (type.isAssignableFrom(e.getClass())) {
                  return;
               }
            }

            throw new AssertionError(buildMessage(expectedExceptions, e));
         }
      }
   }

   private String buildMessage(List<EunitException> expectedExceptions, Throwable actual) {
      StringBuilder sb = new StringBuilder(1024);
      boolean first = true;

      sb.append("Expected one of following exceptions: ");

      for (EunitException expectedException : expectedExceptions) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
         }

         sb.append(expectedException.getType().getName());
      }

      sb.append('.');

      if (actual != null) {
         sb.append(" But was: ");
         sb.append(actual.getClass().getName());
      }

      return sb.toString();
   }
}
