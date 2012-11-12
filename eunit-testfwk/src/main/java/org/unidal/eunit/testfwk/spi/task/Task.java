package org.unidal.eunit.testfwk.spi.task;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.unidal.eunit.model.entity.EunitMethod;

public class Task<T extends ITaskType> implements ITask<T> {
   private T m_type;

   private EunitMethod m_eunitMethod;

   private Map<String, Object> m_attributes;

   public Task(T type, EunitMethod eunitMethod) {
      m_type = type;
      m_eunitMethod = eunitMethod;
   }

   @Override
   public EunitMethod getEunitMethod() {
      return m_eunitMethod;
   }

   public Method getMethod() {
      return m_eunitMethod.getMethod();
   }

   @SuppressWarnings("unchecked")
   public <S> S getAttribute(String name) {
      return m_attributes == null ? null : (S) m_attributes.get(name);
   }

   public T getType() {
      return m_type;
   }

   public boolean hasAttribute(String name) {
      return m_attributes != null && m_attributes.containsKey(name);
   }

   public void setAttribute(String name, Object value) {
      if (m_attributes == null) {
         m_attributes = new HashMap<String, Object>(4);
      }

      m_attributes.put(name, value);
   }

   @Override
   public String toString() {
      return String.format("Task[type=%s, method=%s, attributes=%s]", m_type.getName(), m_eunitMethod == null ? null
            : m_eunitMethod.getName(), m_attributes);
   }
}