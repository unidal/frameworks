package org.unidal.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Service;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class TrackableBeanPostProcessor implements BeanPostProcessor {
   private BeanNaming m_naming = new DefaultBeanNaming();

   private boolean m_useCglib = false;

   @Override
   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      Class<?> beanClass = bean.getClass();

      if (beanClass.isAnnotationPresent(Trackable.class)) {
         TrackableInvocationHandler handler = new TrackableInvocationHandler(m_naming, bean);

         if (m_useCglib) {
            return Enhancer.create(bean.getClass(), handler);
         } else {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), handler);
         }
      }

      return bean;
   }

   @Override
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      return bean;
   }

   protected static interface BeanNaming {
      public String getData(Object instance, Method method, Object[] args);

      public String getName(Object instance, Method method, Object[] args);

      public String getType(Object instance, Method method, Object[] args);
   }

   protected static class DefaultBeanNaming implements BeanNaming {
      @Override
      public String getData(Object instance, Method method, Object[] args) {
         return Arrays.asList(args).toString();
      }

      @Override
      public String getName(Object instance, Method method, Object[] args) {
         Class<?>[] types = method.getParameterTypes();
         StringBuilder sb = new StringBuilder(256);
         boolean first = true;

         sb.append(method.getName());
         sb.append('(');

         for (Class<?> type : types) {
            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            sb.append(type.getSimpleName());
         }

         sb.append(')');

         return sb.toString();
      }

      @Override
      public String getType(Object instance, Method method, Object[] args) {
         Service service = instance.getClass().getAnnotation(Service.class);

         if (service != null) {
            String value = service.value();

            if (value.length() > 0) {
               return value;
            }
         }

         return instance.getClass().getName();
      }
   }

   protected static class TrackableInvocationHandler implements InvocationHandler, MethodInterceptor {
      private BeanNaming m_naming;

      private Object m_bean;

      public TrackableInvocationHandler(BeanNaming naming, Object bean) {
         m_naming = naming;
         m_bean = bean;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return intercept(m_bean, method, args, null);
      }

      @Override
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
         String type = m_naming.getType(m_bean, method, args);
         String name = m_naming.getName(m_bean, method, args);
         String data = m_naming.getData(m_bean, method, args);
         Transaction t = Cat.newTransaction(type, name);

         if (data != null) {
            t.addData(data);
         }

         try {
            if (!method.isAccessible()) {
               method.setAccessible(true);
            }

            Object value = method.invoke(m_bean, args);

            t.setStatus(Message.SUCCESS);
            return value;
         } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
         } finally {
            t.complete();
         }
      }
   }
}
