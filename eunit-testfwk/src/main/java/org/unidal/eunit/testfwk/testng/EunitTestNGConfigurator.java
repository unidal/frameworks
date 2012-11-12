package org.unidal.eunit.testfwk.testng;

import org.unidal.eunit.handler.ExpectedExceptionHandler;
import org.unidal.eunit.handler.ExpectedExceptionsHandler;
import org.unidal.eunit.handler.GroupsHandler;
import org.unidal.eunit.handler.IdHandler;
import org.unidal.eunit.handler.InterceptHandler;
import org.unidal.eunit.handler.RunGroupsHandler;
import org.unidal.eunit.handler.RunIgnoreHandler;
import org.unidal.eunit.handler.ServiceProviderHandler;
import org.unidal.eunit.handler.testng.AfterClassHandler;
import org.unidal.eunit.handler.testng.AfterMethodHandler;
import org.unidal.eunit.handler.testng.BeforeClassHandler;
import org.unidal.eunit.handler.testng.BeforeMethodHandler;
import org.unidal.eunit.handler.testng.TestHandler;
import org.unidal.eunit.invocation.EunitCaseContextFactory;
import org.unidal.eunit.invocation.EunitMethodInvoker;
import org.unidal.eunit.testfwk.ClassProcessor;
import org.unidal.eunit.testfwk.EunitEventListener;
import org.unidal.eunit.testfwk.EunitTaskExecutor;
import org.unidal.eunit.testfwk.junit.EunitJUnitTestCaseBuilder;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.Registry;

public class EunitTestNGConfigurator implements IConfigurator {
   @Override
   public void configure(Registry registry) {
      registry.registerEventListener(EunitEventListener.INSTANCE);
      registry.registerClassProcessor(new ClassProcessor());
      registry.registerTaskExecutors(EunitTaskExecutor.values());
      registry.registerCaseContextFactory(new EunitCaseContextFactory());
      registry.registerMethodInvoker(new EunitMethodInvoker());

      registry.registerTestPlanBuilder(new EunitTestNGTestPlanBuilder());
      registry.registerTestCaseBuilder(new EunitJUnitTestCaseBuilder());

      registry.registerAnnotationHandler(TestHandler.INSTANCE);
      registry.registerAnnotationHandler(BeforeMethodHandler.INSTANCE);
      registry.registerAnnotationHandler(AfterMethodHandler.INSTANCE);
      registry.registerAnnotationHandler(BeforeClassHandler.INSTANCE);
      registry.registerAnnotationHandler(AfterClassHandler.INSTANCE);
      registry.registerAnnotationHandler(RunIgnoreHandler.INSTANCE);
      
      registry.registerAnnotationHandler(ExpectedExceptionHandler.INSTANCE);
      registry.registerAnnotationHandler(ExpectedExceptionsHandler.INSTANCE);
      registry.registerAnnotationHandler(IdHandler.INSTANCE);
      registry.registerAnnotationHandler(GroupsHandler.INSTANCE);
      registry.registerAnnotationHandler(InterceptHandler.INSTANCE);
      registry.registerAnnotationHandler(RunGroupsHandler.INSTANCE);
      registry.registerAnnotationHandler(RunIgnoreHandler.INSTANCE);

      registry.registerMetaAnnotationHandler(ServiceProviderHandler.INSTANCE);
   }
}
