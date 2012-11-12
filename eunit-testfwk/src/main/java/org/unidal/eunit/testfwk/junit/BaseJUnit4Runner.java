package org.unidal.eunit.testfwk.junit;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import org.unidal.eunit.testfwk.CaseContext;
import org.unidal.eunit.testfwk.ClassContext;
import org.unidal.eunit.testfwk.EunitManager;
import org.unidal.eunit.testfwk.EunitRuntimeConfig;
import org.unidal.eunit.testfwk.junit.JUnitTestPlan.Entry;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.ITestCallback;
import org.unidal.eunit.testfwk.spi.ITestClassRunner;
import org.unidal.eunit.testfwk.spi.ITestPlan;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskType;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.IValveChain;
import org.unidal.eunit.testfwk.spi.task.Priority;
import org.unidal.eunit.testfwk.spi.task.SimpleValveChain;
import org.unidal.eunit.testfwk.spi.task.TaskValve;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public abstract class BaseJUnit4Runner extends ParentRunner<Entry> implements ITestClassRunner {
   private String m_methodName;

   private List<Entry> m_children;

   private IClassContext m_ctx;

   private ValveMap m_classValveMap;

   public BaseJUnit4Runner(Class<?> testClass) throws InitializationError {
      super(testClass);

      initialize(testClass);
   }

   public BaseJUnit4Runner(Class<?> testClass, String methodName) throws InitializationError {
      this(testClass);

      m_methodName = methodName;
   }

   protected SimpleValveChain createChain(List<ITask<? extends ITaskType>> tasks) {
      List<IValve<? extends ICaseContext>> valves = new ArrayList<IValve<? extends ICaseContext>>(tasks.size());

      for (ITask<? extends ITaskType> task : tasks) {
         valves.add(new TaskValve(task));
      }

      return new SimpleValveChain(valves);
   }
   
   protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
          boolean isStatic, List<Throwable> errors) {
   }

   @Override
   protected Description describeChild(Entry child) {
      return child.getDescription();
   }

   @Override
   protected List<Entry> getChildren() {
      String methodName = m_methodName;

      if (methodName == null) {
         return m_children;
      } else {
         Entry found = null;

         for (Entry entry : m_children) {
            if (entry.getEunitMethod().getName().equals(methodName)) {
               found = entry;
               break;
            }
         }

         if (found == null) {
            throw new IllegalArgumentException(String.format("No method(%s) found in the %s", methodName, m_ctx.getTestClass()));
         } else {
            return Arrays.asList(found);
         }
      }
   }

   protected IClassContext getClassContext() {
      return m_ctx;
   }

   protected ValveMap getClassValveMap(final RunNotifier notifier) {
      if (m_classValveMap == null) {
         final ValveMap classValveMap = new ValveMap();

         classValveMap.mergeFrom(m_ctx.getTestPlan().getClassValveMap());
         classValveMap.mergeFrom(m_ctx.getRegistry().getClassValveMap());
         classValveMap.addValve(Priority.LOW, new IValve<ICaseContext>() {
            @Override
            public void execute(ICaseContext ctx, IValveChain chain) throws Throwable {
               runSuper(notifier);
            }
         });

         m_classValveMap = classValveMap;
      }

      return m_classValveMap;
   }

   protected abstract IConfigurator getConfigurator();

   protected void initialize(Class<?> testClass) {
      Class<?> namespace = getClass();
      ClassContext ctx = new ClassContext(namespace, testClass);

      EunitRuntimeConfig.INSTANCE.initialize();
      ctx.setRegistry(EunitManager.INSTANCE.initialize(namespace, getConfigurator()));
      ctx.setTestPlan(new JUnitTestPlan());

      EunitManager.INSTANCE.buildPlan(ctx, this);
      m_ctx = ctx;
   }

   @Override
   public void run(final RunNotifier notifier) {
      if (m_ctx.forEunit().getEunitClass().isIgnored()) {
         notifier.fireTestIgnored(getDescription());
         return;
      }

      final ValveMap classValveMap = getClassValveMap(notifier);
      final CaseContext caseContext = new CaseContext(m_ctx, null);

      try {
         classValveMap.getValveChain().executeNext(caseContext);
      } catch (Throwable e) {
         notifier.fireTestFailure(new Failure(getDescription(), e));
      }
   }

   public void runClass() throws Throwable {
      runMethod(null);
   }

   public void runMethod(String methodName) throws Throwable {
      m_methodName = methodName;

      final RunNotifier notifier = new RunNotifier();
      final ErrorListener listener = new ErrorListener();

      notifier.addListener(listener);
      run(notifier);

      if (listener.hasFailures()) {
         throw listener.firstException();
      }
   }

   protected void runSuper(RunNotifier notifier) {
      super.run(notifier);
   }

   @Override
   @SuppressWarnings("unchecked")
   public void setPlan(ITestPlan<? extends ITestCallback> plan, Object children) {
      if (plan instanceof JUnitTestPlan) {
         m_children = (List<Entry>) children;
      } else {
         throw new RuntimeException(String.format("Unsupported test plan(%s)!", plan.getClass().getName()));
      }
   }

   @Override
   protected Statement withAfterClasses(Statement statement) {
      return statement;
   }

   @Override
   protected Statement withBeforeClasses(Statement statement) {
      return statement;
   }

   static class ErrorListener extends RunListener {
      private final List<Throwable> m_exceptions = new ArrayList<Throwable>(3);

      public Throwable firstException() {
         return m_exceptions.get(0);
      }

      public boolean hasFailures() {
         return m_exceptions.size() > 0;
      }

      @Override
      public void testAssumptionFailure(Failure failure) {
         m_exceptions.add(failure.getException());
      }

      @Override
      public void testFailure(Failure failure) throws Exception {
         m_exceptions.add(failure.getException());
      }
   }
}
