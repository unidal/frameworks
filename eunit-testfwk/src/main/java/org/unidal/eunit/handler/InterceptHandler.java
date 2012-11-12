package org.unidal.eunit.handler;

import java.lang.reflect.Method;

import org.unidal.eunit.annotation.Intercept;
import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IDeferredAnnotationHandler;
import org.unidal.eunit.testfwk.spi.ITestCallback;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.ITestPlan.IDeferredAction;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.IValveChain;
import org.unidal.eunit.testfwk.spi.task.Priority;

public enum InterceptHandler implements IDeferredAnnotationHandler<Intercept, Method> {
   INSTANCE;

   @Override
   public IDeferredHandler createDeferredHandler(IClassContext ctx, Intercept meta, Method method) {
      return new InterceptDeferredHandler(this, ctx, meta, method);
   }

   @Override
   public Class<Intercept> getTargetAnnotation() {
      return Intercept.class;
   }

   @Override
   public void handle(final IClassContext ctx, Intercept meta, Method method) {
      EunitClass eunitClass = ctx.forEunit().getEunitClass();
      EunitMethod beforeMethod = null;
      EunitMethod afterMethod = null;
      EunitMethod onErrorMethod = null;

      if (meta.beforeMethod().length() > 0) {
         beforeMethod = eunitClass.findMethod(meta.beforeMethod());
      }

      if (meta.afterMethod().length() > 0) {
         afterMethod = eunitClass.findMethod(meta.afterMethod());
      }

      if (meta.onErrorMethod().length() > 0) {
         onErrorMethod = eunitClass.findMethod(meta.onErrorMethod());
      }

      ctx.getTestPlan().addDeferredAction(new InterceptDeferredAction(onErrorMethod, method, beforeMethod, afterMethod, ctx));
   }

   @Override
   public boolean isAfter() {
      return false;
   }

   @Override
   public String toString() {
      return String.format("%s.%s", getClass().getSimpleName(), name());
   }

   private final class InterceptDeferredAction implements IDeferredAction {
      private final EunitMethod m_onErrorMethod;

      private final Method m_method;

      private final EunitMethod m_beforeMethod;

      private final EunitMethod m_afterMethod;

      private final IClassContext m_ctx;

      private InterceptDeferredAction(EunitMethod onErrorMethod, Method method, EunitMethod beforeMethod, EunitMethod afterMethod,
            IClassContext ctx) {
         m_onErrorMethod = onErrorMethod;
         m_method = method;
         m_beforeMethod = beforeMethod;
         m_afterMethod = afterMethod;
         m_ctx = ctx;
      }

      @Override
      public void execute() {
         ITestCase<? extends ITestCallback> testCase = m_ctx.getTestPlan().getTestCase(m_method);
         InterceptValve valve = new InterceptValve(m_method, m_beforeMethod, m_afterMethod, m_onErrorMethod);

         testCase.getValveMap().addValve(Priority.HIGH, valve);
      }
   }

   static class InterceptDeferredHandler implements IDeferredHandler {
      private InterceptHandler m_handler;

      private IClassContext m_ctx;

      private Intercept m_meta;

      private Method m_method;

      public InterceptDeferredHandler(InterceptHandler handler, IClassContext ctx, Intercept meta, Method method) {
         m_handler = handler;
         m_ctx = ctx;
         m_meta = meta;
         m_method = method;
      }

      @Override
      public void execute() {
         m_handler.handle(m_ctx, m_meta, m_method);
      }
   }

   static class InterceptValve implements IValve<ICaseContext> {
      private Method m_targetMethod;

      private EunitMethod m_beforeMethod;

      private EunitMethod m_afterMethod;

      private EunitMethod m_onErrorMethod;

      public InterceptValve(Method targetMethod, EunitMethod beforeMethod, EunitMethod afterMethod, EunitMethod onErrorMethod) {
         m_targetMethod = targetMethod;
         m_beforeMethod = beforeMethod;
         m_afterMethod = afterMethod;
         m_onErrorMethod = onErrorMethod;
      }

      @Override
      public void execute(ICaseContext ctx, IValveChain chain) throws Throwable {
         Method method = ctx.getEunitMethod().getMethod();

         if (m_targetMethod == method || m_targetMethod.equals(method)) {
            if (m_beforeMethod != null) {
               ctx.invokeWithInjection(m_beforeMethod);
            }

            try {
               chain.executeNext(ctx);
            } catch (Throwable e) {
               if (m_onErrorMethod != null) {
                  ctx.setAttribute(e, null);
                  ctx.invokeWithInjection(m_onErrorMethod);
                  ctx.removeAttribute(e, null);
               } else {
                  throw e;
               }
            } finally {
               if (m_afterMethod != null) {
                  ctx.invokeWithInjection(m_afterMethod);
               }
            }
         } else {
            chain.executeNext(ctx);
         }
      }

      @Override
      public String toString() {
         return String.format("InterceptValve[method=%s, beforeMethod=%s, afterMethod=%s, onErrorMethod=%s]", m_targetMethod
               .getName(), m_beforeMethod == null ? null : m_beforeMethod.getName(),
               m_afterMethod == null ? null : m_afterMethod.getName(), m_onErrorMethod == null ? null : m_onErrorMethod.getName());
      }
   }
}
