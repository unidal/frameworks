package org.unidal.eunit.testfwk.junit;

import org.unidal.eunit.handler.ExpectedExceptionHandler;
import org.unidal.eunit.handler.ExpectedExceptionsHandler;
import org.unidal.eunit.handler.GroupsHandler;
import org.unidal.eunit.handler.IdHandler;
import org.unidal.eunit.handler.InterceptHandler;
import org.unidal.eunit.handler.RunGroupsHandler;
import org.unidal.eunit.handler.RunIgnoreHandler;
import org.unidal.eunit.handler.ServiceProviderHandler;
import org.unidal.eunit.handler.junit.AfterClassHandler;
import org.unidal.eunit.handler.junit.AfterHandler;
import org.unidal.eunit.handler.junit.BeforeClassHandler;
import org.unidal.eunit.handler.junit.BeforeHandler;
import org.unidal.eunit.handler.junit.IgnoreHandler;
import org.unidal.eunit.handler.junit.TestHandler;
import org.unidal.eunit.invocation.EunitCaseContextFactory;
import org.unidal.eunit.invocation.EunitMethodInvoker;
import org.unidal.eunit.invocation.EunitParameterResolver;
import org.unidal.eunit.testfwk.ClassProcessor;
import org.unidal.eunit.testfwk.EunitEventListener;
import org.unidal.eunit.testfwk.EunitTaskExecutor;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.Registry;
import org.unidal.eunit.testfwk.spi.task.Priority;

public class EunitJUnitConfigurator implements IConfigurator {
   @Override
   public void configure(Registry registry) {
      registry.registerEventListener(EunitEventListener.INSTANCE);
      registry.registerClassProcessor(new ClassProcessor());
      registry.registerTaskExecutors(EunitTaskExecutor.values());
      registry.registerCaseContextFactory(new EunitCaseContextFactory());
      registry.registerMethodInvoker(new EunitMethodInvoker());
      registry.registerParamResolver(EunitParameterResolver.INSTANCE);
      
      registry.registerCaseValve(Priority.HIGH, EunitExceptionValve.INSTANCE);

      registry.registerTestPlanBuilder(new EunitJUnitTestPlanBuilder());
      registry.registerTestCaseBuilder(new EunitJUnitTestCaseBuilder());

      registry.registerAnnotationHandler(TestHandler.INSTANCE);
      registry.registerAnnotationHandler(IgnoreHandler.INSTANCE);
      registry.registerAnnotationHandler(BeforeClassHandler.INSTANCE);
      registry.registerAnnotationHandler(AfterClassHandler.INSTANCE);
      registry.registerAnnotationHandler(BeforeHandler.INSTANCE);
      registry.registerAnnotationHandler(AfterHandler.INSTANCE);
      
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
