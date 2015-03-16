package org.unidal.eunit.benchmark.testfwk;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.junit.Test;

import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;
import org.unidal.eunit.benchmark.model.entity.BenchmarkEntity;
import org.unidal.eunit.benchmark.model.entity.CaseEntity;
import org.unidal.eunit.benchmark.model.entity.SuiteEntity;
import org.unidal.eunit.testfwk.ClassContext.ModelContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.event.Event;
import org.unidal.eunit.testfwk.spi.event.IEventListener;

public enum BenchmarkEventListener implements IEventListener {
   INSTANCE;

   private boolean isBenchmarkTestCase(Method method) {
      if (method.isAnnotationPresent(CpuMeta.class) || method.isAnnotationPresent(MemoryMeta.class)) {
         return true;
      }

      if (method.isAnnotationPresent(Test.class)) {
         Class<?> clazz = method.getDeclaringClass();

         return clazz.isAnnotationPresent(CpuMeta.class) || clazz.isAnnotationPresent(MemoryMeta.class);
      }

      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void onEvent(IClassContext context, Event event) {
      AnnotatedElement source = event.getSource();
      ModelContext<BenchmarkEntity> ctx = (ModelContext<BenchmarkEntity>) (ModelContext<?>) context.forModel();

      switch (event.getType()) {
      case BEFORE_CLASS:
         Class<?> clazz = (Class<?>) source;
         BenchmarkEntity benchmark = new BenchmarkEntity();
         SuiteEntity s = new SuiteEntity(clazz);

         ctx.push(s);
         ctx.setModel(benchmark.addSuite(s));
         break;
      case BEFORE_METHOD:
         Method method = (Method) source;
         SuiteEntity suite = ctx.peek();

         if (isBenchmarkTestCase(method)) {
            CaseEntity c = new CaseEntity(method.getName());

            c.setMethod(method);
            suite.addCase(c);
            ctx.push(c);
         } else {
            ctx.push(null);
         }

         break;
      case AFTER_METHOD:
         CaseEntity c = ctx.pop();

         if (c != null) {
            SuiteEntity parent = ctx.peek();

            if (parent.getCpu() != null && c.getCpu() == null) {
               c.setCpu(parent.getCpu());
            }

            if (parent.getMemory() != null && c.getMemory() == null) {
               c.setMemory(parent.getMemory());
            }
         }

         break;
      case AFTER_CLASS:
         ctx.pop();
         break;
      default:
         break;
      }
   }

}
