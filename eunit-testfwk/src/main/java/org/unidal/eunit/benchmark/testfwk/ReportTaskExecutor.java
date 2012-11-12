package org.unidal.eunit.benchmark.testfwk;

import org.unidal.eunit.testfwk.EunitTaskType;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.task.ITaskExecutor;

public enum ReportTaskExecutor implements ITaskExecutor<EunitTaskType> {
   XML_REPORT(EunitTaskType.AFTER_CLASS) {
      @Override
      public void execute(ICaseContext ctx) {
         System.out.println(ctx.getClassContext().forModel().getModel());
      }
   };

   private EunitTaskType m_type;

   private ReportTaskExecutor(EunitTaskType type) {
      m_type = type;
   }

   @Override
   public EunitTaskType getTaskType() {
      return m_type;
   }
}