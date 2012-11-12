package org.unidal.eunit.benchmark.testfwk.junit;

import org.unidal.eunit.benchmark.model.entity.BenchmarkEntity;
import org.unidal.eunit.benchmark.model.entity.CaseEntity;
import org.unidal.eunit.benchmark.model.entity.CpuEntity;
import org.unidal.eunit.benchmark.model.entity.MemoryEntity;
import org.unidal.eunit.benchmark.model.entity.SuiteEntity;
import org.unidal.eunit.benchmark.testfwk.CpuTaskType;
import org.unidal.eunit.benchmark.testfwk.MemoryTaskType;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.junit.EunitJUnitTestCaseBuilder;
import org.unidal.eunit.testfwk.junit.JUnitCallback;
import org.unidal.eunit.testfwk.junit.JUnitTestCase;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IModelContext;
import org.unidal.eunit.testfwk.spi.ITestCase;

public class BenchmarkJUnitTestCaseBuilder extends EunitJUnitTestCaseBuilder {
   @Override
   public ITestCase<JUnitCallback> build(IClassContext classContext, EunitMethod eunitMethod) {
      JUnitTestCase testCase = new JUnitTestCase(eunitMethod);
      IModelContext<BenchmarkEntity> ctx = classContext.forModel();
      BenchmarkEntity benchmark = ctx.getModel();
      SuiteEntity suite = benchmark.findSuite(classContext.getTestClass());
      CaseEntity c = suite.findCase(eunitMethod.getName());

      if (c != null) {
         MemoryEntity memory = c.getMemory();
         CpuEntity cpu = c.getCpu();

         if (memory != null) {
            testCase.addTask(MemoryTaskType.START, eunitMethod);
            testCase.addTask(MemoryTaskType.WARMUP, eunitMethod, "loops", memory.getWarmups());
            testCase.addTask(MemoryTaskType.EXECUTE, eunitMethod, "loops", memory.getLoops());
            testCase.addTask(MemoryTaskType.END, eunitMethod);
         }

         if (cpu != null) {
            testCase.addTask(CpuTaskType.START, eunitMethod);
            testCase.addTask(CpuTaskType.WARMUP, eunitMethod, "loops", cpu.getWarmups());
            testCase.addTask(CpuTaskType.EXECUTE, eunitMethod, "loops", cpu.getLoops());
            testCase.addTask(CpuTaskType.END, eunitMethod);
         }
      } else {
         return super.build(classContext, eunitMethod);
      }

      return testCase;
   }
}
