package org.unidal.eunit.benchmark.testfwk;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.unidal.eunit.benchmark.model.entity.BenchmarkEntity;
import org.unidal.eunit.benchmark.model.entity.CaseEntity;
import org.unidal.eunit.benchmark.model.entity.CpuEntity;
import org.unidal.eunit.benchmark.model.entity.SuiteEntity;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IModelContext;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskExecutor;
import org.unidal.eunit.testfwk.spi.task.ITaskType;

public enum CpuTaskExecutor implements ITaskExecutor<CpuTaskType> {
   START(CpuTaskType.START) {
      @Override
      public void execute(ICaseContext ctx) {
         CaseEntity testCase = getCaseEntity(ctx);
         CpuEntity cpu = new CpuEntity();

         testCase.setCpu(cpu);
      }
   },

   WARMUP(CpuTaskType.WARMUP) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         final Object instance = ctx.getTestInstance();
         final ITask<ITaskType> task = ctx.getTask();
         final Integer loops = task.getAttribute("loops");
         final Method method = task.getEunitMethod().getMethod();
         final ThreadMXBean mxbean = ManagementFactory.getThreadMXBean();

         // do GC so that it will not interfere CPU time
         GarbageCollectorHelper.INSTANCE.runGC();

         final long startCpuFirstTime = mxbean.getCurrentThreadCpuTime();
         final long startElapsedFirstTime = System.nanoTime();

         try {
            for (int i = 0; i < loops; i++) {
               method.invoke(instance);
            }
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }

         final long endCpuFirstTime = mxbean.getCurrentThreadCpuTime();
         final long endElapsedFirstTime = System.nanoTime();

         final CaseEntity testCase = getCaseEntity(ctx);
         final CpuEntity cpu = testCase.getCpu();

         cpu.setWarmups(loops);
         cpu.setCpuFirstTime(endCpuFirstTime - startCpuFirstTime);
         cpu.setElapsedFirstTime(endElapsedFirstTime - startElapsedFirstTime);
      }
   },

   EXECUTE(CpuTaskType.EXECUTE) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         final Object instance = ctx.getTestInstance();
         final ITask<ITaskType> task = ctx.getTask();
         final Integer loops = task.getAttribute("loops");
         final Method method = task.getEunitMethod().getMethod();
         final ThreadMXBean mxbean = ManagementFactory.getThreadMXBean();

         // do GC so that it will not interfere CPU time
         GarbageCollectorHelper.INSTANCE.runGC();

         final long startCpuTime = mxbean.getCurrentThreadCpuTime();
         final long startElapsedTime = System.nanoTime();

         try {
            for (int i = 0; i < loops; i++) {
               method.invoke(instance);
            }
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }

         final long endCpuTime = mxbean.getCurrentThreadCpuTime();
         final long endElapsedTime = System.nanoTime();
         final CaseEntity testCase = getCaseEntity(ctx);
         final CpuEntity cpu = testCase.getCpu();

         cpu.setLoops(loops);
         cpu.setCpuTotalTime(endCpuTime - startCpuTime);
         cpu.setElapsedTotalTime(endElapsedTime - startElapsedTime);
         cpu.setCpuTime(cpu.getCpuTotalTime() / loops);
         cpu.setElapsedTime(cpu.getElapsedTotalTime() / loops);

         // adjustment if can
         if (cpu.getCpuFirstTime() > cpu.getWarmups() * cpu.getCpuTime()) {
            cpu.setCpuFirstTime(cpu.getCpuFirstTime() - (cpu.getWarmups() - 1) * cpu.getCpuTime());
         }

         if (cpu.getElapsedFirstTime() > cpu.getWarmups() * cpu.getElapsedTime()) {
            cpu.setElapsedFirstTime(cpu.getElapsedFirstTime() - (cpu.getWarmups() - 1) * cpu.getElapsedTime());
         }
      }
   },

   END(CpuTaskType.END) {
      @Override
      public void execute(ICaseContext ctx) {
         // nothing here
      }
   };

   private CpuTaskType m_type;

   private CpuTaskExecutor(CpuTaskType type) {
      m_type = type;
   }

   protected CaseEntity getCaseEntity(ICaseContext ctx) {
      final IClassContext classContext = ctx.getClassContext();
      final EunitMethod eunitMethod = ctx.getEunitMethod();
      final IModelContext<BenchmarkEntity> model = classContext.forModel();
      final SuiteEntity suite = model.getModel().findSuite(classContext.getTestClass());
      final CaseEntity testCase = suite.findCase(eunitMethod.getName());

      return testCase;
   }

   @Override
   public CpuTaskType getTaskType() {
      return m_type;
   }
}