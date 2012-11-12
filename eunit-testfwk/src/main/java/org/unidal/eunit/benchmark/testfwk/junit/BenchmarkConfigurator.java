package org.unidal.eunit.benchmark.testfwk.junit;

import org.unidal.eunit.benchmark.handler.CpuHandler;
import org.unidal.eunit.benchmark.handler.MemoryHandler;
import org.unidal.eunit.benchmark.testfwk.BenchmarkEventListener;
import org.unidal.eunit.benchmark.testfwk.CpuTaskExecutor;
import org.unidal.eunit.benchmark.testfwk.MemoryTaskExecutor;
import org.unidal.eunit.benchmark.testfwk.ReportTaskExecutor;
import org.unidal.eunit.testfwk.junit.EunitJUnitConfigurator;
import org.unidal.eunit.testfwk.spi.Registry;

public class BenchmarkConfigurator extends EunitJUnitConfigurator {
   public static final BenchmarkConfigurator INSTANCE = new BenchmarkConfigurator();

   private BenchmarkConfigurator() {
   }

   public void configure(Registry registry) {
      super.configure(registry);

      // override event listener and test case builder
      registry.registerEventListener(BenchmarkEventListener.INSTANCE);
      registry.registerTestCaseBuilder(new BenchmarkJUnitTestCaseBuilder());

      registry.registerAnnotationHandler(CpuHandler.INSTANCE);
      registry.registerAnnotationHandler(MemoryHandler.INSTANCE);

      registry.registerTaskExecutors(CpuTaskExecutor.values());
      registry.registerTaskExecutors(MemoryTaskExecutor.values());
      registry.registerTaskExecutors(ReportTaskExecutor.values());
   }
}
