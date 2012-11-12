package org.unidal.eunit.testfwk.spi.task;

import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.Registry;

public class TaskValve implements IValve<ICaseContext> {
   private ITask<? extends ITaskType> m_task;

   private boolean m_before;

   public TaskValve(ITask<? extends ITaskType> task) {
      this(task, true);
   }

   public TaskValve(ITask<? extends ITaskType> task, boolean before) {
      m_task = task;
      m_before = before;
   }

   @Override
   public void execute(ICaseContext ctx, IValveChain chain) throws Throwable {
      if (m_before) {
         Registry registry = ctx.getClassContext().getRegistry();

         ctx.setTask(m_task);
         registry.getTaskExecutor(m_task.getType()).execute(ctx);

         chain.executeNext(ctx);
      } else {
         Registry registry = ctx.getClassContext().getRegistry();

         try {
            chain.executeNext(ctx);
         } finally {
            ctx.setTask(m_task);
            registry.getTaskExecutor(m_task.getType()).execute(ctx);
         }
      }
   }

   @Override
   public String toString() {
      return String.format("TaskValve[task=%s, before=%s]", m_task, m_before);
   }
}
