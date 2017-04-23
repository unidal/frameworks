package org.unidal.lookup.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.unidal.helper.Reflects;
import org.unidal.helper.Splitters;
import org.unidal.lookup.container.lifecycle.ComponentLifecycle;
import org.unidal.lookup.container.model.entity.ComponentModel;

public class ComponentBox<T> {
   // role hint => component
   private Map<String, T> m_map = new HashMap<String, T>();

   private ComponentLifecycle m_lifecycle;

   public ComponentBox(ComponentKey key, T component) {
      m_map.put(key.getRoleHint(), component);
   }

   public ComponentBox(ComponentLifecycle lifecycle) {
      m_lifecycle = lifecycle;
   }

   @SuppressWarnings("unchecked")
   private T createInstance(ComponentModel model) throws ComponentLookupException {
      Class<?> clazz = Reflects.forClass().getClass(model.getImplementation());
      String message;

      try {
         if (clazz == null) {
            message = String.format("Class(%s) is not found!", model.getImplementation());
         } else {
            return (T) clazz.newInstance();
         }
      } catch (InstantiationException e) {
         message = String.format("Class(%s) is not accessible!", clazz.getName());
      } catch (IllegalAccessException e) {
         message = String.format("Constructor of class(%s) is not accessible!", clazz.getName());
      }

      throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
   }

   @SuppressWarnings("unchecked")
   private T getEnumField(ComponentModel model) throws ComponentLookupException {
      Class<?> clazz = Reflects.forClass().getClass(model.getImplementation());
      String message;

      if (clazz.isEnum()) {
         Object[] values = Reflects.forMethod().invokeStaticMethod(clazz, "values");
         List<String> parts = Splitters.by(':').split(model.getRoleHint());
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

      throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
   }

   public T lookup(ComponentModel model) throws ComponentLookupException {
      String roleHint = model.getRoleHint();
      T component = m_map.get(roleHint);

      if (component == null) {
         if (model.isSingleton()) {
            component = createInstance(model);
         } else if (model.isPerLookup()) {
            component = createInstance(model);
         } else if (model.isEnum()) {
            component = getEnumField(model);
         } else {
            throw new UnsupportedOperationException("Unknown instantiation strategy of component: " + model);
         }

         m_lifecycle.start(component, model);

         if (!model.isPerLookup()) {
            m_map.put(roleHint, component);
         }
      }

      return component;
   }

   @Override
   public String toString() {
      return String.format("%s[components=%s]", getClass().getSimpleName(), m_map);
   }
}
