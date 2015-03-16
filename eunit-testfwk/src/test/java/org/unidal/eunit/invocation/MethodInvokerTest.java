package org.unidal.eunit.invocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.annotation.Id;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.CaseContext;
import org.unidal.eunit.testfwk.ClassContext;
import org.unidal.eunit.testfwk.EunitManager;
import org.unidal.eunit.testfwk.junit.EunitJUnitConfigurator;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;
import org.unidal.eunit.testfwk.spi.Registry;
import org.unidal.eunit.testfwk.spi.event.Event;
import org.unidal.eunit.testfwk.spi.event.IEventListener;

@RunWith(EunitJUnit4Runner.class)
public class MethodInvokerTest {
   private static IClassContext m_ctx;

   @BeforeClass
   public static void setup() {
      Class<?> clazz = MethodInvokerTest.class;
      ClassContext ctx = new ClassContext(clazz, clazz);
      Registry registry = EunitManager.INSTANCE.initialize(clazz, new EunitJUnitConfigurator());

      ctx.setRegistry(registry);
      registry.registerParamResolver(new MockParameterResolver());
      registry.registerEventListener(new MockParameterEventListener());
      registry.getClassProcessor().process(ctx);

      m_ctx = ctx;
   }

   public String m1(String first, Boolean third, Integer second, Double forth, @MockId("i5") Object fifth,
         @Id("i6") Object sixth) {
      String result = first + ":" + second + ":" + third + ":" + forth + ":" + fifth + ":" + sixth;

      return result;
   }

   @Test
   public void test() throws Throwable {
      EunitMethod method = m_ctx.forEunit().getEunitClass().findMethod("m1");
      ICaseContext ctx = new CaseContext(m_ctx, method);

      ctx.setAttribute("f1", "i1");
      ctx.setAttribute(Integer.valueOf(2), null);
      ctx.setAttribute(Boolean.TRUE, "i3");
      ctx.setAttribute(Object.class, "Hello", "i5");
      ctx.setAttribute(Object.class, "World", "i6");

      Object result = ctx.invokeWithInjection(method);

      Assert.assertEquals("f1:2:true:12.34:Hello:World", String.valueOf(result));
   }

   static class MockParameterEventListener implements IEventListener {
      @Override
      public void onEvent(IClassContext classContext, Event event) {
         AnnotatedElement source = event.getSource();
         IEunitContext ctx = classContext.forEunit();

         switch (event.getType()) {
         case BEFORE_PARAMETER:
            MockId mockId = source.getAnnotation(MockId.class);

            if (mockId != null) {
               EunitParameter eunitParameter = ctx.peek();

               eunitParameter.setId(mockId.value());
            }

            break;
         default:
            break;
         }
      }
   }

   static class MockParameterResolver implements IParameterResolver<CaseContext> {
      @Override
      public boolean matches(CaseContext ctx, EunitParameter eunitParameter) {
         return eunitParameter.getType() == Double.class;
      }

      @Override
      public Object resolve(CaseContext ctx, EunitParameter eunitParameter) {
         return new Double(12.34d);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.PARAMETER)
   public static @interface MockId {
      String value();
   }
}
