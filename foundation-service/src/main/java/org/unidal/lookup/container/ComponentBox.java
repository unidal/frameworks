package org.unidal.lookup.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Reflects;
import org.unidal.helper.Splitters;
import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.container.lifecycle.ComponentLifecycle;
import org.unidal.lookup.container.model.entity.ComponentModel;

public class ComponentBox<T> {
   // component cache
   // role hint => component
   private Map<String, T> m_components = new HashMap<String, T>();

   private ComponentLifecycle m_lifecycle;

   public ComponentBox(ComponentLifecycle lifecycle) {
      m_lifecycle = lifecycle;
   }

   @SuppressWarnings("unchecked")
   private T createInstance(ComponentModel model) throws ComponentLookupException {
      Class<?> clazz = Reflects.forClass().getClass(model.getImplementation());
      Throwable cause = null;
      String message;

      try {
         if (clazz == null) {
            message = String.format("Class(%s) is not found!", model.getImplementation());
         } else {
            return (T) clazz.newInstance();
         }
      } catch (InstantiationException e) {
         message = String.format("Class(%s) is not accessible!", clazz.getName());
         cause = e;
      } catch (IllegalAccessException e) {
         message = String.format("Constructor of class(%s) is not accessible!", clazz.getName());
         cause = e;
      } catch (NoClassDefFoundError e) {
         message = String.format("Class(%s) is not found!", clazz.getName());
         cause = e;
      }

      throw new ComponentLookupException(message, model.getRole(), model.getHint(), cause);
   }

   public void destroy() {
      for (T component : m_components.values()) {
         m_lifecycle.stop(component);
      }

      m_components.clear();
   }

   @SuppressWarnings("unchecked")
   private T getEnumField(ComponentModel model) throws ComponentLookupException {
      Class<?> clazz = Reflects.forClass().getClass(model.getImplementation());
      String message;

      if (clazz.isEnum()) {
         Object[] values = Reflects.forMethod().invokeStaticMethod(clazz, "values");
         List<String> parts = Splitters.by(':').split(model.getHint());
         String field = parts.get(0);

         for (Object value : values) {
            if (value.toString().equals(field)) {
               return (T) value;
            }
         }

         message = String.format("No field(%s) of class(%s) is found!", field, clazz.getName());
      } else {
         message = String.format("Class(%s) is not enum!", clazz.getName());
      }

      throw new ComponentLookupException(message, model.getRole(), model.getHint());
   }

   public T lookup(ComponentModel model) throws ComponentLookupException {
      String roleHint = model.getHint();
      T component = m_components.get(roleHint);

      if (component == null) {
         m_lifecycle.onStarting(model);

         if (model.isSingleton()) {
            component = createInstance(model);
         } else if (model.isPerLookup()) {
            component = createInstance(model);
         } else if (model.isEnum()) {
            component = getEnumField(model);
         } else {
            throw new UnsupportedOperationException("Unknown instantiation strategy of component: " + model);
         }

         if (!model.isPerLookup()) {
            m_components.put(roleHint, component);
         }

         m_lifecycle.start(component, model);
         m_lifecycle.onStarted(model);
      }

      return component;
   }

   public ComponentBox<T> register(ComponentKey key, T component) {
      m_components.put(key.getRoleHint(), component);
      return this;
   }

   @Override
   public String toString() {
      return String.format("%s[components=%s]", getClass().getSimpleName(), m_components.size());
   }
}
