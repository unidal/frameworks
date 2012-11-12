package org.unidal.eunit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.junit.BaseJUnit4Runner;
import org.unidal.eunit.testfwk.junit.EunitJUnitConfigurator;
import org.unidal.eunit.testfwk.junit.JUnitCallback;
import org.unidal.eunit.testfwk.junit.JUnitTestPlan.Entry;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.ICaseContextFactory;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public class EunitJUnit4Runner extends BaseJUnit4Runner {
   public EunitJUnit4Runner(Class<?> clazz) throws InitializationError {
      super(clazz);
   }

   public EunitJUnit4Runner(Class<?> clazz, String methodName) throws InitializationError {
      super(clazz, methodName);
   }

   protected void execute(EunitMethod eunitMethod, ITestCase<JUnitCallback> testCase) throws Throwable {
      final IClassContext classContext = getClassContext();
      final ICaseContextFactory factory = classContext.getRegistry().getCaseContextFactory();
      final ICaseContext ctx = factory.createContext(classContext, eunitMethod);
      final ValveMap valveMap = new ValveMap();

      valveMap.mergeFrom(testCase.getValveMap());
      valveMap.mergeFrom(classContext.getTestPlan().getCaseValveMap());
      valveMap.mergeFrom(classContext.getRegistry().getCaseValveMap());
      valveMap.getValveChain().executeNext(ctx);
   }

   protected IConfigurator getConfigurator() {
      return new EunitJUnitConfigurator();
   }

   @Override
   protected void runChild(Entry child, RunNotifier notifier) {
      final Description description = child.getDescription();
      final EunitMethod eunitMethod = child.getEunitMethod();
      final JUnitCallback callback = new JUnitCallback(notifier);

      callback.setDescription(description);

      if (eunitMethod.isIgnored()) {
         callback.onIgnored();
      } else {
         callback.onStarted();

         try {
            execute(eunitMethod, child.getTestCase());

            callback.onFinished();
         } catch (Throwable e) {
            callback.onFailure(e);
         }
      }
   }
}
