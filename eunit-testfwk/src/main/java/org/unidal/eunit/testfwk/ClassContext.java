package org.unidal.eunit.testfwk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.ITestCallback;
import org.unidal.eunit.testfwk.spi.ITestPlan;
import org.unidal.eunit.testfwk.spi.Registry;
import org.unidal.eunit.testfwk.spi.filter.IGroupFilter;

public class ClassContext implements IClassContext {
   private Class<?> m_testClass;

   private Registry m_registry;

   private IEunitContext m_eunitContext = new EunitContext();

   private IModelContext<Object> m_modelContext = new ModelContext<Object>();

   private ITestPlan<? extends ITestCallback> m_testPlan;

   public ClassContext(Class<?> runnerClass, Class<?> testClass) {
      m_testClass = testClass;
   }

   @Override
   public IEunitContext forEunit() {
      return m_eunitContext;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <M> IModelContext<M> forModel() {
      return (IModelContext<M>) m_modelContext;
   }

   @Override
   public Registry getRegistry() {
      return m_registry;
   }

   @Override
   public Class<?> getTestClass() {
      return m_testClass;
   }

   public ITestPlan<? extends ITestCallback> getTestPlan() {
      return m_testPlan;
   }

   public void setRegistry(Registry registry) {
      m_registry = registry;
   }

   public void setTestPlan(ITestPlan<? extends ITestCallback> testPlan) {
      m_testPlan = testPlan;
   }

   public static class EunitContext implements IEunitContext {
      private EunitClass m_eunitClass;

      private Map<Class<?>, Map<String, Object>> m_attributes = new HashMap<Class<?>, Map<String, Object>>();

      private Stack<Object> m_stack = new Stack<Object>();

      private IGroupFilter m_groupFilter;

      @Override
      public List<Object> getAttributes(Class<?> targetType, String id) {
         List<Object> attributes = new ArrayList<Object>();

         for (Map.Entry<Class<?>, Map<String, Object>> e : m_attributes.entrySet()) {
            Class<?> type = e.getKey();

            if (targetType.isAssignableFrom(type)) {
               Map<String, Object> map = e.getValue();

               if (id == null) {
                  attributes.addAll(map.values());
               } else {
                  Object value = map.get(id);

                  if (value != null) {
                     attributes.add(value);
                  }
               }
            }
         }

         return attributes;
      }

      @Override
      public EunitClass getEunitClass() {
         return m_eunitClass;
      }

      public IGroupFilter getGroupFilter() {
         return m_groupFilter;
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T peek() {
         return (T) m_stack.peek();
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T pop() {
         return (T) m_stack.pop();
      }

      @Override
      public void push(Object object) {
         m_stack.push(object);
      }

      @Override
      public Object removeAttribute(Class<?> type, String id) {
         Map<String, Object> map = m_attributes.get(type);

         if (map != null) {
            return map.remove(id);
         } else {
            return null;
         }
      }

      @Override
      public void setAttribute(Class<?> type, Object value, String id) {
         Map<String, Object> map = m_attributes.get(type);

         if (map == null) {
            map = new HashMap<String, Object>();
            m_attributes.put(type, map);
         }

         map.put(id, value);
      }

      public void setEunitClass(EunitClass eunitClass) {
         m_eunitClass = eunitClass;
      }

      public void setGroupFilter(IGroupFilter groupFilter) {
         m_groupFilter = groupFilter;
      }
   }

   public static class ModelContext<M> implements IModelContext<M> {
      private M m_model;

      private Stack<Object> m_stack = new Stack<Object>();

      @Override
      public M getModel() {
         return m_model;
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T peek() {
         return (T) m_stack.peek();
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T pop() {
         return (T) m_stack.pop();
      }

      @Override
      public void push(Object object) {
         m_stack.push(object);
      }

      public void setModel(M model) {
         m_model = model;
      }
   }
}
