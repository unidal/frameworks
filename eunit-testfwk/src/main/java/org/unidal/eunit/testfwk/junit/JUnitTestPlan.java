package org.unidal.eunit.testfwk.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.ITestClassRunner;
import org.unidal.eunit.testfwk.spi.ITestPlan;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskType;
import org.unidal.eunit.testfwk.spi.task.Priority;
import org.unidal.eunit.testfwk.spi.task.TaskValve;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public class JUnitTestPlan implements ITestPlan<JUnitCallback> {
   private List<Entry> m_entries = new ArrayList<Entry>();

   private ValveMap m_classValveMap = new ValveMap();

   private ValveMap m_caseValveMap = new ValveMap();

   private List<IDeferredAction> m_actions = new ArrayList<IDeferredAction>();

   @Override
   public ITestPlan<JUnitCallback> addAfter(ITask<? extends ITaskType> task) {
      m_caseValveMap.addValve(Priority.MIDDLE, new TaskValve(task, false));
      return this;
   }

   @Override
   public ITestPlan<JUnitCallback> addAfterClass(ITask<? extends ITaskType> task) {
      m_classValveMap.addValve(Priority.MIDDLE, new TaskValve(task, false));
      return this;
   }

   @Override
   public ITestPlan<JUnitCallback> addBefore(ITask<? extends ITaskType> task) {
      m_caseValveMap.addValve(Priority.MIDDLE, new TaskValve(task, true));
      return this;
   }

   @Override
   public ITestPlan<JUnitCallback> addBeforeClass(ITask<? extends ITaskType> task) {
      m_classValveMap.addValve(Priority.MIDDLE, new TaskValve(task, true));
      return this;
   }

   @Override
   public ITestPlan<JUnitCallback> addDeferredAction(IDeferredAction action) {
      m_actions.add(action);
      return this;
   }

   @Override
   public ITestPlan<JUnitCallback> addTestCase(EunitMethod eunitMethod, ITestCase<JUnitCallback> testCase) {
      Class<?> targetClass = eunitMethod.getEunitClass().getType();
      Description child = Description.createTestDescription(targetClass, eunitMethod.getName());

      m_entries.add(new Entry(child, eunitMethod, testCase));
      return this;
   }

   @Override
   public void bindTo(ITestClassRunner runner) {
      if (runner instanceof Runner) {
         runner.setPlan(this, m_entries);
      }
   }

   @Override
   public void executeDeferredActions() {
      for (IDeferredAction action : m_actions) {
         action.execute();
      }
   }

   protected Entry findEntry(Method method) {
      int size = method.getParameterTypes().length;

      for (Entry entry : m_entries) {
         EunitMethod m = entry.getEunitMethod();

         if (m.getName().equals(method.getName())) {
            if (m.getParameters().size() == size) {
               return entry;
            }
         }
      }

      throw new IllegalStateException(String.format("No Entry found for method(%s)!", method.getName()));
   }

   public ValveMap getCaseValveMap() {
      return m_caseValveMap;
   }

   public ValveMap getClassValveMap() {
      return m_classValveMap;
   }

   public ITestCase<JUnitCallback> getTestCase(Method method) {
      Entry entry = findEntry(method);

      return entry.getTestCase();
   }

   public static class Entry {
      private Description m_description;

      private EunitMethod m_eunitMethod;

      private ITestCase<JUnitCallback> m_testCase;

      public Entry(Description description, EunitMethod eunitMethod, ITestCase<JUnitCallback> testCase) {
         m_description = description;
         m_eunitMethod = eunitMethod;
         m_testCase = testCase;
      }

      public Description getDescription() {
         return m_description;
      }

      public EunitMethod getEunitMethod() {
         return m_eunitMethod;
      }

      public ITestCase<JUnitCallback> getTestCase() {
         return m_testCase;
      }
   }
}
