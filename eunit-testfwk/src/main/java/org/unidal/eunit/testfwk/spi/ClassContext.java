package org.unidal.eunit.testfwk.spi;

import java.util.Stack;

import org.unidal.eunit.model.entity.EunitClass;

public class ClassContext {
   private Class<?> m_testClass;

   private Registry m_registry;

   private EunitContext m_eunitContext = new EunitContext();

   private ModelContext<Object> m_modelContext = new ModelContext<Object>();

   private Object m_testInstance;

   public ClassContext(Class<?> testClass) {
      m_testClass = testClass;

      try {
         m_testInstance = testClass.newInstance();
      } catch (Exception e) {
         throw new RuntimeException(String.format(
               "Unable to create instance of test class(%s), please make sure it has a public zero-argument constructor defined!",
               testClass.getName()));
      }
   }

   public EunitContext forEunit() {
      return m_eunitContext;
   }

   @SuppressWarnings("unchecked")
   public <M> ModelContext<M> forModel() {
      return (ModelContext<M>) m_modelContext;
   }

   public Registry getRegistry() {
      return m_registry;
   }

   public Class<?> getTestClass() {
      return m_testClass;
   }

   public void setRegistry(Registry registry) {
      m_registry = registry;
   }

   public static class EunitContext {
      private EunitClass m_eunitClass;

      private Stack<Object> m_stack = new Stack<Object>();

      public EunitClass getEunitClass() {
         return m_eunitClass;
      }

      @SuppressWarnings("unchecked")
      public <T> T peek() {
         return (T) m_stack.peek();
      }

      @SuppressWarnings("unchecked")
      public <T> T pop() {
         return (T) m_stack.pop();
      }

      public void push(Object object) {
         m_stack.push(object);
      }

      public void setEunitClass(EunitClass eunitClass) {
         m_eunitClass = eunitClass;
      }
   }

   public static class ModelContext<M> {
      private M m_model;

      private Stack<Object> m_stack = new Stack<Object>();

      public M getModel() {
         return m_model;
      }

      @SuppressWarnings("unchecked")
      public <T> T peek() {
         return (T) m_stack.peek();
      }

      @SuppressWarnings("unchecked")
      public <T> T pop() {
         return (T) m_stack.pop();
      }

      public void push(Object object) {
         m_stack.push(object);
      }

      public void setModel(M model) {
         m_model = model;
      }
   }

   public Object getTestInstance() {
      return m_testInstance;
   }
}
