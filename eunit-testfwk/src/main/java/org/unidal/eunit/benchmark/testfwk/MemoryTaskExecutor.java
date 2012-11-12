package org.unidal.eunit.benchmark.testfwk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.unidal.eunit.benchmark.model.entity.BenchmarkEntity;
import org.unidal.eunit.benchmark.model.entity.CaseEntity;
import org.unidal.eunit.benchmark.model.entity.MemoryEntity;
import org.unidal.eunit.benchmark.model.entity.SuiteEntity;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IModelContext;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskExecutor;
import org.unidal.eunit.testfwk.spi.task.ITaskType;

public enum MemoryTaskExecutor implements ITaskExecutor<MemoryTaskType> {
   START(MemoryTaskType.START) {
      @Override
      public void execute(ICaseContext ctx) {
         CaseEntity testCase = getCaseEntity(ctx);
         MemoryEntity memory = new MemoryEntity();

         testCase.setMemory(memory);
      }
   },

   WARMUP(MemoryTaskType.WARMUP) {
      private int m_overhead = 48; // win: 64, mac:48

      private long startUsedMemory;

      private long endUsedMemory;

      private int index;

      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         final Object instance = ctx.getTestInstance();
         final Object[] args = new Object[0];
         final ITask<ITaskType> task = ctx.getTask();
         final Integer loops = task.getAttribute("loops");
         final Method method = task.getEunitMethod().getMethod();
         final GarbageCollectorHelper gc = GarbageCollectorHelper.INSTANCE;

         gc.runGC();

         startUsedMemory = gc.usedMemory();

         try {
            for (index = loops - 1; index >= 0; index--) {
               method.invoke(instance, args);
            }
         } catch (final InvocationTargetException e) {
            throw e.getCause();
         }

         gc.runGC();

         endUsedMemory = gc.usedMemory();

         final CaseEntity testCase = getCaseEntity(ctx);
         final MemoryEntity memory = testCase.getMemory();

         memory.setWarmups(loops);
         memory.setPermanentFootprint(endUsedMemory - startUsedMemory - m_overhead);
      }
   },

   EXECUTE(MemoryTaskType.EXECUTE) {
      private long startGcCount;

      private long startGcAmount;

      private long startGcTime;

      private long startUsedMemory;

      private long endGcCount;

      private long endGcAmount;

      private long endGcTime;

      private long endUsedMemory;

      private Object[] instances;

      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         GarbageCollectorHelper gc = GarbageCollectorHelper.INSTANCE;
         final Object instance = ctx.getTestInstance();
         final Object[] args = new Object[0];
         final ITask<ITaskType> task = ctx.getTask();
         final Integer loops = task.getAttribute("loops");
         final Method method = task.getEunitMethod().getMethod();

         instances = new Object[loops];

         gc.runGC();

         startUsedMemory = gc.usedMemory();
         startGcCount = gc.getGcCount();
         startGcAmount = gc.getGcAmount();
         startGcTime = gc.getGcTime();

         try {
            for (int i = loops - 1; i >= 0; i--) {
               instances[i] = method.invoke(instance, args);
            }
         } catch (final InvocationTargetException e) {
            throw e.getCause();
         }

         gc.runGC();

         instances = null;
         endUsedMemory = gc.usedMemory();
         endGcCount = gc.getGcCount();
         endGcAmount = gc.getGcAmount();
         endGcTime = gc.getGcTime();

         final CaseEntity testCase = getCaseEntity(ctx);
         final MemoryEntity memory = testCase.getMemory();

         memory.setLoops(loops);
         memory.setGcCount(endGcCount - startGcCount);
         memory.setGcAmount(endGcAmount - startGcAmount);
         memory.setGcTime(endGcTime - startGcTime);
         memory.setTotalFootprint(endUsedMemory - startUsedMemory);
         memory.setFootprint(Math.round((double) memory.getTotalFootprint() / loops));
      }
   },

   END(MemoryTaskType.END) {
      @Override
      public void execute(ICaseContext ctx) {
         // nothing here
      }
   };

   private MemoryTaskType m_type;

   private MemoryTaskExecutor(MemoryTaskType type) {
      m_type = type;
   }

   @Override
   public MemoryTaskType getTaskType() {
      return m_type;
   }

   protected CaseEntity getCaseEntity(ICaseContext ctx) {
      final IClassContext classContext = ctx.getClassContext();
      final EunitMethod eunitMethod = ctx.getEunitMethod();
      final IModelContext<BenchmarkEntity> model = classContext.forModel();
      final SuiteEntity suite = model.getModel().findSuite(classContext.getTestClass());
      final CaseEntity testCase = suite.findCase(eunitMethod.getName());

      return testCase;
   }
}