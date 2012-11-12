package org.unidal.eunit.benchmark;

import org.junit.runners.model.InitializationError;

import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.benchmark.testfwk.junit.BenchmarkConfigurator;
import org.unidal.eunit.testfwk.spi.IConfigurator;

public class BenchmarkClassRunner extends EunitJUnit4Runner {
   public BenchmarkClassRunner(Class<?> clazz) throws InitializationError {
      super(clazz);
   }

   public BenchmarkClassRunner(Class<?> clazz, String methodName) throws InitializationError {
      super(clazz, methodName);
   }

   @Override
   protected IConfigurator getConfigurator() {
      return BenchmarkConfigurator.INSTANCE;
   }
}
