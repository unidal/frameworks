package org.unidal.eunit.testfwk.spi;

import java.util.List;

import org.unidal.eunit.model.entity.EunitClass;

public interface IClassContext {
   public IEunitContext forEunit();

   public <M> IModelContext<M> forModel();

   public Registry getRegistry();

   public Class<?> getTestClass();

   public ITestPlan<? extends ITestCallback> getTestPlan();

   public static interface IEunitContext {
      public List<Object> getAttributes(Class<?> targetType, String id);

      public EunitClass getEunitClass();

      public <T> T peek();

      public <T> T pop();

      public void push(Object object);

      public Object removeAttribute(Class<?> type, String id);

      public void setAttribute(Class<?> type, Object value, String id);
   }

   public interface IModelContext<M> {
      public M getModel();

      public <T> T peek();

      public <T> T pop();

      public void push(Object object);
   }
}