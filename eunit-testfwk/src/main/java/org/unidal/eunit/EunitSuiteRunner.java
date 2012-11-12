package org.unidal.eunit;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import org.unidal.eunit.annotation.RunGroups;
import org.unidal.eunit.annotation.RunIgnore;
import org.unidal.eunit.testfwk.EunitRuntimeConfig;
import org.unidal.eunit.testfwk.spi.filter.GroupFilter;
import org.unidal.eunit.testfwk.spi.filter.RunOption;

public class EunitSuiteRunner extends Suite {
   public EunitSuiteRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
      super(klass, new DecoratedRunnerBuilder(builder));
   }

   public void run() throws Throwable {
      final RunNotifier notifier = new RunNotifier();
      final List<Throwable> exceptions = new ArrayList<Throwable>();
      final RunListener listener = new RunListener() {
         @Override
         public void testAssumptionFailure(Failure failure) {
            exceptions.add(failure.getException());
         }

         @Override
         public void testFailure(Failure failure) throws Exception {
            exceptions.add(failure.getException());
         }
      };

      notifier.addListener(listener);
      run(notifier);

      if (!exceptions.isEmpty()) {
         Throwable exception = exceptions.get(0);

         throw exception;
      }
   }

   static class DecoratedRunnerBuilder extends RunnerBuilder {
      private RunnerBuilder m_builder;

      public DecoratedRunnerBuilder(RunnerBuilder builder) {
         m_builder = builder;
      }

      @Override
      public Runner runnerForClass(Class<?> testClass) throws Throwable {
         return m_builder.runnerForClass(testClass);
      }

      @Override
      public List<Runner> runners(Class<?> suiteClass, Class<?>[] children) throws InitializationError {
         RunGroups groupMeta = suiteClass.getAnnotation(RunGroups.class);
         RunIgnore ignoreMeta = suiteClass.getAnnotation(RunIgnore.class);
         boolean needFilter = EunitRuntimeConfig.INSTANCE.getGroupFilter() == null && groupMeta != null;
         boolean needIgnore = EunitRuntimeConfig.INSTANCE.getRunOption() == null && ignoreMeta != null;

         if (needFilter) {
            EunitRuntimeConfig.INSTANCE.setGroupFilter(new GroupFilter(groupMeta.include(), groupMeta.exclude()));
         }

         if (needIgnore) {
            EunitRuntimeConfig.INSTANCE.setRunOption(ignoreMeta.runAll() ? RunOption.ALL_CASES : RunOption.IGNORED_CASES_ONLY);
         }

         try {
            return m_builder.runners(suiteClass, children);
         } finally {
            if (needFilter) {
               EunitRuntimeConfig.INSTANCE.setGroupFilter(null);
            }

            if (needIgnore) {
               EunitRuntimeConfig.INSTANCE.setRunOption(RunOption.TEST_CASES_ONLY);
            }
         }
      }

      @Override
      public Runner safeRunnerForClass(Class<?> testClass) {
         return m_builder.safeRunnerForClass(testClass);
      }
   }
}
