package org.unidal.eunit;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.testng.IAnnotationTransformer;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.annotations.ITestAnnotation;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import org.unidal.eunit.annotation.Groups;
import org.unidal.eunit.annotation.RunGroups;
import org.unidal.eunit.annotation.RunIgnore;
import org.unidal.eunit.annotation.testng.ConfigurationFile;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.EunitRuntimeConfig;
import org.unidal.eunit.testfwk.junit.BaseJUnit4Runner;
import org.unidal.eunit.testfwk.junit.JUnitCallback;
import org.unidal.eunit.testfwk.junit.JUnitTestPlan.Entry;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.filter.GroupFilter;
import org.unidal.eunit.testfwk.spi.filter.IGroupFilter;
import org.unidal.eunit.testfwk.spi.filter.RunOption;
import org.unidal.eunit.testfwk.testng.EunitTestNGConfigurator;

public class EunitTestNGRunner extends BaseJUnit4Runner {
   private IClassContext m_ctx;

   public EunitTestNGRunner(Class<?> clazz) throws InitializationError {
      super(clazz);

      m_ctx = getClassContext();
   }

   public EunitTestNGRunner(Class<?> clazz, String methodName) throws InitializationError {
      super(clazz, methodName);
   }

   protected IConfigurator getConfigurator() {
      return new EunitTestNGConfigurator();
   }

   @Override
   protected void runChild(Entry child, RunNotifier notifier) {
      throw new RuntimeException("Not used!");
   }

   @Override
   protected void runSuper(RunNotifier notifier) {
      JUnitCallback callback = new JUnitCallback(notifier);
      TestNG testng = new TestNG();

      testng.addListener(new MockInvokedMethodListener(callback, getChildren()));
      testng.setTestClasses(new Class[] { m_ctx.getTestClass() });
      testng.setVerbose(0);

      setupConfigurationFile(testng);
      setupAnnotationTransformer(testng);

      testng.run();
   }

   protected void setupAnnotationTransformer(TestNG testng) {
      RunOption option = EunitRuntimeConfig.INSTANCE.getRunOption();
      IGroupFilter filter = EunitRuntimeConfig.INSTANCE.getGroupFilter();

      if (option == null) {
         RunIgnore meta = m_ctx.getTestClass().getAnnotation(RunIgnore.class);

         if (meta != null) {
            option = meta.runAll() ? RunOption.ALL_CASES : RunOption.IGNORED_CASES_ONLY;
         }
      }

      if (filter == null) {
         RunGroups meta = m_ctx.getTestClass().getAnnotation(RunGroups.class);

         if (meta != null) {
            filter = new GroupFilter(meta.include(), meta.exclude());
         }
      }

      if (option != null || filter != null) {
         testng.setAnnotationTransformer(new EunitAnnotationTransformer(option, filter));
      }
   }

   protected void setupConfigurationFile(TestNG testng) {
      Class<?> testClass = m_ctx.getTestClass();
      ConfigurationFile meta = testClass.getAnnotation(ConfigurationFile.class);

      if (meta != null) {
         String xmlFile = meta.value();
         InputStream in = testClass.getResourceAsStream(xmlFile);

         if (in == null) {
            String xmlPath;

            if (xmlFile.startsWith("/")) {
               xmlPath = xmlFile;
            } else {
               xmlPath = '/' + testClass.getPackage().getName().replace('.', '/') + '/' + xmlFile;
            }

            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlPath);
         }

         if (in != null) {
            // throw new RuntimeException(String.format("XML configuration file(%s) not found!", xmlFile));
            // } else {
            try {
               List<XmlSuite> suites = new Parser(in).parseToList();

               testng.setXmlSuites(suites);
            } catch (Exception e) {
               throw new RuntimeException(String.format("Unable to parse XML configuration file(%s)! error: %s.",
                     xmlFile, e.getMessage()), e);
            }
         }
      }
   }

   static class EunitAnnotationTransformer implements IAnnotationTransformer {
      private RunOption m_option;

      private IGroupFilter m_filter;

      public EunitAnnotationTransformer(RunOption option, IGroupFilter filter) {
         m_option = option;
         m_filter = filter;
      }

      private EunitMethod buildEunitMethod(Method method) {
         EunitMethod eunitMethod = new EunitMethod(method.getName());
         Groups methodGroups = method.getAnnotation(Groups.class);
         Groups classGroups = method.getDeclaringClass().getAnnotation(Groups.class);

         if (methodGroups != null) {
            for (String group : methodGroups.value()) {
               eunitMethod.addGroup(group);
            }
         }

         if (classGroups != null) {
            for (String group : classGroups.value()) {
               eunitMethod.addGroup(group);
            }
         }

         return eunitMethod;
      }

      @Override
      public void transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass,
            @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod) {
         if (testMethod != null) {
            EunitMethod eunitMethod = m_filter == null ? null : buildEunitMethod(testMethod);

            if (m_filter == null || m_filter.matches(eunitMethod)) {
               switch (m_option) {
               case IGNORED_CASES_ONLY:
                  annotation.setEnabled(!annotation.getEnabled());
                  break;
               case ALL_CASES:
                  annotation.setEnabled(true);
                  break;
               default:
                  break;
               }
            } else {
               annotation.setEnabled(false);
            }
         }
      }
   }

   static class MockInvokedMethodListener implements IInvokedMethodListener {
      private JUnitCallback m_callback;

      private Map<String, Description> m_descriptions = new HashMap<String, Description>();

      private Set<String> m_failed = new HashSet<String>();

      public MockInvokedMethodListener(JUnitCallback callback, List<Entry> entries) {
         m_callback = callback;

         for (Entry entry : entries) {
            String methodName = entry.getEunitMethod().getName();
            Description description = entry.getDescription();

            m_descriptions.put(methodName, description);

            if (entry.getEunitMethod().isIgnored()) {
               m_callback.setDescription(description);
               m_callback.onIgnored();
            }
         }
      }

      @Override
      public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
         if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            Description description = m_descriptions.get(methodName);

            if (description != null) {
               m_callback.setDescription(description);

               switch (testResult.getStatus()) {
               case ITestResult.SUCCESS:
                  if (!m_failed.contains(methodName)) {
                     m_callback.onFinished();
                  }
                  break;
               case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                  m_callback.onFailure(testResult.getThrowable()); // TODO
                  m_failed.add(methodName);
                  break;
               case ITestResult.FAILURE:
                  m_callback.onFailure(testResult.getThrowable());
                  m_failed.add(methodName);
                  break;
               }
            } else {
               throw new RuntimeException("Unknown method: " + methodName);
            }
         }
      }

      @Override
      public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
         if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            Description description = m_descriptions.get(methodName);

            if (description != null) {
               m_callback.setDescription(description);

               if (!m_failed.contains(methodName)) {
                  m_callback.onStarted();
               }
            } else {
               throw new RuntimeException("Unknown method: " + methodName);
            }
         }
      }
   }
}
