package org.unidal.eunit.testfwk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.unidal.eunit.invocation.IMethodInvoker;
import org.unidal.eunit.invocation.IParameterResolver;
import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskType;

public class CaseContext implements ICaseContext {
   private IClassContext m_ctx;

   private EunitMethod m_eunitMethod;

   private ITask<? extends ITaskType> m_task;

   private Object m_testInstance;

   public CaseContext(IClassContext ctx, EunitMethod eunitMethod) {
      m_ctx = ctx;
      m_eunitMethod = eunitMethod;

      Class<?> testClass = ctx.getTestClass();

      try {
         Constructor<?> c = testClass.getDeclaredConstructor();

         if (!c.isAccessible()) {
            c.setAccessible(true);
         }

         m_testInstance = c.newInstance();
      } catch (Exception e) {
         throw new RuntimeException(String.format(
               "Unable to create instance of test class(%s), please make sure it has a public zero-argument constructor defined!",
               testClass.getName()));
      }
   }

   protected Object checkAndGetFirstAttribute(List<Object> attributes, String displayName) {
      int size = attributes.size();

      if (size == 0) {
         // should return null?
         throw new RuntimeException(String.format("No attribute found for %s!", displayName));
      } else if (size == 1) {
         return attributes.get(0);
      } else {
         StringBuilder sb = new StringBuilder(1024);

         sb.append("Multiple attributes found for ").append(displayName);
         sb.append("! They are [").append(attributes).append("].");

         throw new RuntimeException(sb.toString());
      }
   }

   @Override
   public Object findAttributeFor(EunitParameter eunitParameter) {
      List<IParameterResolver<ICaseContext>> resolvers = m_ctx.getRegistry().getParamResolvers();
      Class<?> targetType = eunitParameter.getType();
      String id = eunitParameter.getId();
      List<Object> result = m_ctx.forEunit().getAttributes(targetType, id);

      for (IParameterResolver<ICaseContext> resolver : resolvers) {
         if (resolver.matches(this, eunitParameter)) {
            result.add(resolver.resolve(this, eunitParameter));
         }
      }

      return checkAndGetFirstAttribute(result,
            String.format("parameter(id=%s, index=%s, type=%s)", id, eunitParameter.getIndex(), targetType));
   }

   @Override
   public Object getAttribute(Class<?> targetType, String id) {
      List<Object> attributes = m_ctx.forEunit().getAttributes(targetType, id);

      return checkAndGetFirstAttribute(attributes, String.format("type(%s, %s)", targetType.getName(), id));
   }

   @Override
   public IClassContext getClassContext() {
      return m_ctx;
   }

   @Override
   public EunitClass getEunitClass() {
      return m_ctx.forEunit().getEunitClass();
   }

   public EunitMethod getEunitMethod() {
      return m_eunitMethod;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <M> M getModel() {
      return (M) m_ctx.forModel().getModel();
   }

   @SuppressWarnings("unchecked")
   public <T extends ITaskType> ITask<T> getTask() {
      return (ITask<T>) m_task;
   }

   @Override
   public Object getTestInstance() {
      return m_testInstance;
   }

   @Override
   public Object invokeWithInjection(EunitMethod eunitMethod) throws Throwable {
      Method method = eunitMethod.getMethod();

      if (!method.isAccessible()) {
         method.setAccessible(true);
      }

      if (method.getParameterTypes().length == 0) {
         try {
            return method.invoke(m_testInstance);
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }
      } else {
         IMethodInvoker invoker = m_ctx.getRegistry().getMethodInvoker();

         return invoker.invoke(this, eunitMethod);
      }
   }

   @Override
   public Object removeAttribute(Class<?> type, String id) {
      return m_ctx.forEunit().removeAttribute(type, id);
   }

   @Override
   public Object removeAttribute(Object value, String id) {
      return removeAttribute(value.getClass(), id);
   }

   @Override
   public void setAttribute(Class<?> type, Object value, String id) {
      m_ctx.forEunit().setAttribute(type, value, id);
   }

   @Override
   public void setAttribute(Object value, String id) {
      setAttribute(value.getClass(), value, id);
   }

   public <T extends ITaskType> void setTask(ITask<T> task) {
      m_task = task;
   }
}
