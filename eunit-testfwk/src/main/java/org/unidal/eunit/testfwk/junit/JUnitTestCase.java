package org.unidal.eunit.testfwk.junit;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskType;
import org.unidal.eunit.testfwk.spi.task.Priority;
import org.unidal.eunit.testfwk.spi.task.Task;
import org.unidal.eunit.testfwk.spi.task.TaskValve;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public class JUnitTestCase implements ITestCase<JUnitCallback> {
   private EunitMethod m_eunitMethod;

   private ValveMap m_valveMap = new ValveMap();

   public JUnitTestCase(EunitMethod eunitMethod) {
      m_eunitMethod = eunitMethod;
   }

   public JUnitTestCase addTask(ITask<? extends ITaskType> task) {
      m_valveMap.addValve(Priority.LOW, new TaskValve(task, true));
      return this;
   }

   public JUnitTestCase addTask(ITaskType type, EunitMethod eunitMethod, Object... nameAndValuePairs) {
      Task<ITaskType> task = new Task<ITaskType>(type, eunitMethod);
      int len = nameAndValuePairs.length;

      if (len % 2 != 0) {
         throw new IllegalArgumentException("Attribute names and values must be paired");
      }

      for (int i = 0; i < len; i += 2) {
         String name = (String) nameAndValuePairs[i];
         Object value = nameAndValuePairs[i + 1];

         task.setAttribute(name, value);
      }

      return addTask(task);
   }

   public EunitMethod getEunitMethod() {
      return m_eunitMethod;
   }

   @Override
   public ValveMap getValveMap() {
      return m_valveMap;
   }
}
