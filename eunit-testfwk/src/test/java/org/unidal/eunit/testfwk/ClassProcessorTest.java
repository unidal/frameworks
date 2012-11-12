package org.unidal.eunit.testfwk;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Stack;

import org.junit.Assert;

import org.junit.Test;

import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IConfigurator;
import org.unidal.eunit.testfwk.spi.IMetaAnnotationHandler;
import org.unidal.eunit.testfwk.spi.Parameter;
import org.unidal.eunit.testfwk.spi.Registry;

public class ClassProcessorTest {
   private static Stack<String> s_stack = new Stack<String>();

   protected void check(Class<?> testClass, String expected) {
      Class<?> namespace = getClass();
      ClassContext ctx = new ClassContext(namespace, testClass);

      ctx.setRegistry(EunitManager.INSTANCE.initialize(namespace, getConfigurator()));

      s_stack.clear();

      new ClassProcessor().process(ctx);

      Assert.assertEquals(expected, s_stack.toString());
   }

   protected IConfigurator getConfigurator() {
      return new IConfigurator() {
         @Override
         public void configure(Registry registry) {
            registry.registerAnnotationHandler(new MockHandler1());
            registry.registerAnnotationHandler(new MockHandler2());
            registry.registerAnnotationHandler(new MockHandler3());
            registry.registerAnnotationHandler(new MockHandler4());
            registry.registerAnnotationHandler(new MockHandler5());
            registry.registerAnnotationHandler(new MockHandler6());

            registry.registerMetaAnnotationHandler(new MockProviderHandler1());
         }
      };
   }

   @Test
   public void testC1() {
      check(C1.class, "[MockMeta1, MockProvider1:MockMeta11:false, MockProvider1:MockMeta12:false, MockMeta3, MockMeta5, MockMeta6, MockMeta4, MockMeta5, MockMeta6, MockMeta2, MockProvider1:MockMeta11:true, MockProvider1:MockMeta12:true]");
   }

   static class BaseAnnotationHandler<T extends Annotation, S extends AnnotatedElement> implements IAnnotationHandler<T, S> {
      private Class<T> m_type;

      private boolean m_after;

      public BaseAnnotationHandler(Class<T> type, boolean after) {
         m_type = type;
         m_after = after;
      }

      @Override
      public Class<T> getTargetAnnotation() {
         return m_type;
      }

      @Override
      public void handle(IClassContext ctx, T meta, S target) {
         s_stack.push(meta.annotationType().getSimpleName());
      }

      @Override
      public boolean isAfter() {
         return m_after;
      }
   }

   static class BaseMetaAnnotationHandler<T extends Annotation, S extends AnnotatedElement> implements IMetaAnnotationHandler<T, S> {
      private Class<T> m_type;

      public BaseMetaAnnotationHandler(Class<T> type) {
         m_type = type;
      }

      @Override
      public Class<T> getTargetAnnotation() {
         return m_type;
      }

      @Override
      public void handle(IClassContext ctx, Annotation annotation, T meta, S target, boolean after) {
         s_stack.push(meta.annotationType().getSimpleName() + ":" + annotation.annotationType().getSimpleName() + ":" + after);
      }
   }

   @MockMeta1
   @MockMeta2
   @MockMeta11
   @MockMeta12
   static class C1 {
      @MockMeta3
      @MockMeta4
      public void t1(@MockMeta5 Object p1, @MockMeta6 String p2) {
         s_stack.add("t1");
      }

      public void t2(@MockMeta5 Object p1, @MockMeta6 String p2) {
         s_stack.add("t2");
      }
   }

   static class MockHandler1 extends BaseAnnotationHandler<MockMeta1, Class<?>> {
      public MockHandler1() {
         super(MockMeta1.class, false);
      }
   }

   static class MockHandler2 extends BaseAnnotationHandler<MockMeta2, Class<?>> {
      public MockHandler2() {
         super(MockMeta2.class, true);
      }
   }

   static class MockHandler3 extends BaseAnnotationHandler<MockMeta3, Method> {
      public MockHandler3() {
         super(MockMeta3.class, false);
      }
   }

   static class MockHandler4 extends BaseAnnotationHandler<MockMeta4, Method> {
      public MockHandler4() {
         super(MockMeta4.class, true);
      }
   }

   static class MockHandler5 extends BaseAnnotationHandler<MockMeta5, Parameter> {
      public MockHandler5() {
         super(MockMeta5.class, false);
      }
   }

   static class MockHandler6 extends BaseAnnotationHandler<MockMeta6, Parameter> {
      public MockHandler6() {
         super(MockMeta6.class, true);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   static @interface MockMeta1 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   @MockProvider1
   static @interface MockMeta11 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   @MockProvider1
   static @interface MockMeta12 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   static @interface MockMeta2 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   static @interface MockMeta3 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   static @interface MockMeta4 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.PARAMETER)
   static @interface MockMeta5 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.PARAMETER)
   static @interface MockMeta6 {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.ANNOTATION_TYPE)
   static @interface MockProvider1 {
   }

   static class MockProviderHandler1 extends BaseMetaAnnotationHandler<MockProvider1, Class<?>> {
      public MockProviderHandler1() {
         super(MockProvider1.class);
      }
   }

}
