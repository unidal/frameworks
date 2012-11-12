package org.unidal.eunit.testfwk.junit;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.unidal.eunit.model.entity.EunitException;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.IValveChain;

public enum EunitExceptionValve implements IValve<ICaseContext> {
   INSTANCE;

   private void assertException(EunitException expectedException, Throwable actual) {
      String expectedMessage = expectedException.getMessage();
      String expectedPattern = expectedException.getPattern();
      String message = actual.getMessage();

      if (expectedMessage != null && expectedMessage.length() > 0) {
         assertEquals(String.format("Exception message is not matched. Matched exception type %s for %s.",
               expectedException.getType().getName(), actual.getClass().getName()), expectedMessage, message);
      }

      if (expectedPattern != null && expectedPattern.length() > 0) {
         MessageFormat format = new MessageFormat(expectedPattern);

         try {
            format.parse(message);
         } catch (Exception e) {
            assertEquals(
                  String.format("Exception message(%s) does not match the expected pattern(%s)!", message, expectedPattern),
                  expectedPattern, message);
         }
      }
   }

   private void buildDistanceMap(Map<Class<?>, Integer> map, Class<?> clazz, int distance) {
      map.put(clazz, distance);

      Class<?> superClass = clazz.getSuperclass();

      if (superClass != Object.class) {
         buildDistanceMap(map, superClass, distance + 1);
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
         }
         catch(AssertionError ae) {
            throw ae;
         }
         catch (Throwable e) {
            EunitException eunitException = findBestMatchedException(expectedExceptions, e);

            if (eunitException != null) {
               assertException(eunitException, e);
            } else {
               throw new AssertionError(buildMessage(expectedExceptions, e));
            }
         }
      }
   }

   private EunitException findBestMatchedException(List<EunitException> expectedExceptions, Throwable e) {
      EunitException eunitException = null;
      int distance = Integer.MAX_VALUE;

      Map<Class<?>, Integer> map = new LinkedHashMap<Class<?>, Integer>();

      buildDistanceMap(map, e.getClass(), 0);

      for (EunitException expectedException : expectedExceptions) {
         Class<?> type = expectedException.getType();

         if (type.isAssignableFrom(e.getClass())) {
            Integer d = map.get(type);

            if (d != null && d.intValue() < distance) {
               eunitException = expectedException;
               distance = d;
            }
         }
      }

      return eunitException;
   }
}
