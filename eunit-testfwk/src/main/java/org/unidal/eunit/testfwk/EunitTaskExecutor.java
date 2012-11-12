package org.unidal.eunit.testfwk;

import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.task.ITaskExecutor;

public enum EunitTaskExecutor implements ITaskExecutor<EunitTaskType> {
   BEFORE_CLASS(EunitTaskType.BEFORE_CLASS) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         // do nothing by default
      }
   },

   METHOD(EunitTaskType.METHOD) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         ctx.invokeWithInjection(ctx.getTask().getEunitMethod());
      }
   },

   CASE(EunitTaskType.TEST_CASE) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         ctx.invokeWithInjection(ctx.getTask().getEunitMethod());
      }
   },

   AFTER_CLASS(EunitTaskType.AFTER_CLASS) {
      @Override
      public void execute(ICaseContext ctx) throws Throwable {
         // do nothing by default
      }
   };

   private EunitTaskType m_type;

   private EunitTaskExecutor(EunitTaskType type) {
      m_type = type;
   }

   @Override
   public EunitTaskType getTaskType() {
      return m_type;
   }
}