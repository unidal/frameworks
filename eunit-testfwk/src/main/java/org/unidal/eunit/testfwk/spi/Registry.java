package org.unidal.eunit.testfwk.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.eunit.invocation.IMethodInvoker;
import org.unidal.eunit.invocation.IParameterResolver;
import org.unidal.eunit.testfwk.spi.event.IEventListener;
import org.unidal.eunit.testfwk.spi.task.ITaskExecutor;
import org.unidal.eunit.testfwk.spi.task.ITaskType;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.Priority;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public class Registry {
   private Class<?> m_namespace;

   private Map<ElementType, List<IAnnotationHandler<?, ?>>> m_handlers = new HashMap<ElementType, List<IAnnotationHandler<?, ?>>>();

   private Map<ElementType, List<IMetaAnnotationHandler<?, ?>>> m_metaHandlers = new HashMap<ElementType, List<IMetaAnnotationHandler<?, ?>>>();

   private Map<ITaskType, ITaskExecutor<ITaskType>> m_executors = new HashMap<ITaskType, ITaskExecutor<ITaskType>>();

   private IClassProcessor m_classProcessor;

   private List<IEventListener> m_listeners = new ArrayList<IEventListener>();

   private ITestPlanBuilder<? extends ITestCallback> m_testPlanBuilder;

   private ITestCaseBuilder<? extends ITestCallback> m_testCaseBuilder;

   private IMethodInvoker m_methodInvoker;

   private ICaseContextFactory m_caseContextFactory;

   private List<IParameterResolver<ICaseContext>> m_paramResolvers = new ArrayList<IParameterResolver<ICaseContext>>();

   private ValveMap m_caseValveMap = new ValveMap();

   private ValveMap m_classValveMap = new ValveMap();

   public Registry(Class<?> namespace) {
      m_namespace = namespace;
   }

   private void addAnnotationHandler(ElementType type, IAnnotationHandler<?, ?> handler) {
      List<IAnnotationHandler<?, ?>> list = m_handlers.get(type);

      if (list == null) {
         list = new ArrayList<IAnnotationHandler<?, ?>>();
         m_handlers.put(type, list);
      }

      if (!list.contains(handler)) {
         list.add(handler);
      }
   }

   private void addMetaAnnotationHandler(ElementType type, IMetaAnnotationHandler<?, ?> handler) {
      List<IMetaAnnotationHandler<?, ?>> list = m_metaHandlers.get(type);

      if (list == null) {
         list = new ArrayList<IMetaAnnotationHandler<?, ?>>();
         m_metaHandlers.put(type, list);
      }

      if (!list.contains(handler)) {
         list.add(handler);
      }
   }

   public List<IAnnotationHandler<?, ?>> getAnnotationHandlers(ElementType type) {
      List<IAnnotationHandler<?, ?>> list = m_handlers.get(type);

      if (list == null) {
         return Collections.emptyList();
      } else {
         return list;
      }
   }

   public ICaseContextFactory getCaseContextFactory() {
      return m_caseContextFactory;
   }

   public ValveMap getCaseValveMap() {
      return m_caseValveMap;
   }

   public IClassProcessor getClassProcessor() {
      return m_classProcessor;
   }

   public ValveMap getClassValveMap() {
      return m_classValveMap;
   }

   public List<IEventListener> getListeners() {
      return m_listeners;
   }

   public List<IMetaAnnotationHandler<?, ?>> getMetaAnnotationHandlers(ElementType type) {
      List<IMetaAnnotationHandler<?, ?>> list = m_metaHandlers.get(type);

      if (list == null) {
         return Collections.emptyList();
      } else {
         return list;
      }
   }

   public IMethodInvoker getMethodInvoker() {
      return m_methodInvoker;
   }

   public Class<?> getNamespace() {
      return m_namespace;
   }

   public List<IParameterResolver<ICaseContext>> getParamResolvers() {
      return m_paramResolvers;
   }

   public ITaskExecutor<ITaskType> getTaskExecutor(ITaskType type) {
      ITaskExecutor<ITaskType> executor = m_executors.get(type);

      if (executor == null) {
         throw new IllegalStateException(String.format("No task executor registered for task type(%s)!", type));
      }

      return executor;
   }

   public ITestCaseBuilder<? extends ITestCallback> getTestCaseBuilder() {
      return m_testCaseBuilder;
   }

   public ITestPlanBuilder<? extends ITestCallback> getTestPlanBuilder() {
      return m_testPlanBuilder;
   }

   public void registerAnnotationHandler(IAnnotationHandler<?, ?> handler) {
      Class<?> annotation = handler.getTargetAnnotation();
      Target target = annotation.getAnnotation(Target.class);

      if (target == null) {
         throw new IllegalArgumentException(String.format("No @Target applied to annotation %s!", annotation));
      }

      for (ElementType type : target.value()) {
         addAnnotationHandler(type, handler);
      }
   }

   public void registerCaseContextFactory(ICaseContextFactory caseContextFactory) {
      m_caseContextFactory = caseContextFactory;
   }

   public void registerCaseValve(Priority priority, IValve<? extends ICaseContext> valve) {
      m_caseValveMap.addValve(priority, valve);
   }

   public void registerCaseValve(Priority priority, IValve<? extends ICaseContext> valve, boolean append) {
      m_caseValveMap.addValve(priority, valve, append);
   }

   public void registerClassProcessor(IClassProcessor classProcessor) {
      m_classProcessor = classProcessor;
   }

   public void registerClassValve(IValve<? extends ICaseContext> valve) {
      m_classValveMap.addValve(Priority.LOW, valve);
   }

   public void registerClassValve(Priority priority, IValve<? extends ICaseContext> valve) {
      m_classValveMap.addValve(priority, valve);
   }

   public void registerClassValve(Priority priority, IValve<? extends ICaseContext> valve, boolean append) {
      m_classValveMap.addValve(priority, valve, append);
   }

   public void registerEventListener(IEventListener listener) {
      if (!m_listeners.contains(listener)) {
         m_listeners.add(listener);
      }
   }

   public void registerMetaAnnotationHandler(IMetaAnnotationHandler<?, ?> handler) {
      Class<?> annotation = handler.getTargetAnnotation();
      Target target = annotation.getAnnotation(Target.class);

      if (target == null) {
         throw new IllegalArgumentException(String.format("No @Target applied to annotation %s!", annotation));
      }

      for (ElementType type : target.value()) {
         addMetaAnnotationHandler(type, handler);
      }
   }

   public void registerMethodInvoker(IMethodInvoker methodInvoker) {
      m_methodInvoker = methodInvoker;
   }

   @SuppressWarnings("unchecked")
   public void registerParamResolver(IParameterResolver<? extends ICaseContext> resolver) {
      if (!m_paramResolvers.contains(resolver)) {
         m_paramResolvers.add((IParameterResolver<ICaseContext>) resolver);
      }
   }

   public <T extends ITaskType> void registerTaskExecutors(ITaskExecutor<T>... executors) {
      for (ITaskExecutor<T> executor : executors) {
         @SuppressWarnings("unchecked")
         ITaskExecutor<ITaskType> e = (ITaskExecutor<ITaskType>) executor;

         m_executors.put(e.getTaskType(), e);
      }
   }

   public void registerTestCaseBuilder(ITestCaseBuilder<? extends ITestCallback> testCaseBuilder) {
      m_testCaseBuilder = testCaseBuilder;
   }

   public void registerTestPlanBuilder(ITestPlanBuilder<? extends ITestCallback> testPlanBuilder) {
      m_testPlanBuilder = testPlanBuilder;
   }
}
