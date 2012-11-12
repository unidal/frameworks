package org.unidal.eunit;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runners.model.InitializationError;

import org.unidal.eunit.testfwk.junit.BaseJUnit4Runner;

public class BaseJUnit4RunnerTest {
   protected void checkClass(Class<?> testClass) throws Exception {
      checkClass(testClass, null);
   }

   protected void checkClass(Class<?> testClass, final Class<?> expectedException) throws Exception {
      final BaseJUnit4Runner runner = createRunner(testClass);
      Throwable actual = null;

      try {
         runner.runMethod(null);
      } catch (Throwable e) {
         actual = e;
      }

      checkException(expectedException, actual);
   }

   protected void checkException(final Class<?> expectedException, Throwable actual) throws AssertionError, Error {
      if (actual == null && expectedException != null) {
         throw new AssertionError(String.format("Exception exception(%s) to be thrown!", expectedException.getName()));
      } else if (actual != null) {
         if (expectedException == null) {
            if (actual instanceof Error) {
               throw (Error) actual;
            } else {
               throw new RuntimeException("Unsupported exception: " + actual);
            }
         } else if (actual.getClass() != expectedException) {
            throw new AssertionError(String.format("Exception exception(%s) to be thrown, but was %s!",
                  expectedException.getName(), actual.getClass().getName()));
         }
      }
   }

   protected void checkMethod(Class<?> testClass, String methodName) throws Exception {
      checkMethod(testClass, methodName, null);
   }

   protected void checkMethod(Class<?> testClass, String methodName, final Class<?> expectedException) throws Exception {
      final BaseJUnit4Runner runner = createRunner(testClass);
      Throwable actual = null;

      try {
         runner.runMethod(methodName);
      } catch (Throwable e) {
         actual = e;
      }

      checkException(expectedException, actual);
   }

   protected void checkSuite(Class<?> testClass) throws Exception {
      checkSuite(testClass, null);
   }

   protected void checkSuite(Class<?> testClass, final Class<?> expectedException) throws Exception {
      final EunitSuiteRunner runner = new EunitSuiteRunner(testClass, new AllDefaultPossibilitiesBuilder(true));
      Throwable actual = null;

      try {
         runner.run();
      } catch (Throwable e) {
         actual = e;
      }

      checkException(expectedException, actual);
   }

   protected BaseJUnit4Runner createRunner(Class<?> testClass) throws InitializationError {
      return new EunitJUnit4Runner(testClass);
   }

}
