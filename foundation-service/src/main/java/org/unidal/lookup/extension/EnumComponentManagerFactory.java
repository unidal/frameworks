package org.unidal.lookup.extension;

import java.util.List;

import org.apache.xbean.recipe.ObjectRecipe;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.builder.XBeanComponentBuilder;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.AbstractComponentManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.unidal.helper.Reflects;
import org.unidal.helper.Splitters;

public class EnumComponentManagerFactory implements ComponentManagerFactory {
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public ComponentManager<?> createComponentManager(MutablePlexusContainer container,
         LifecycleHandler lifecycleHandler, ComponentDescriptor componentDescriptor, String role, String roleHint) {
      return new EnumComponentManager(container, lifecycleHandler, componentDescriptor, role, roleHint);
   }

   public String getId() {
      return "enum";
   }

   static class EnumComponentManager<T> extends AbstractComponentManager<T> {
      public EnumComponentManager(MutablePlexusContainer container, LifecycleHandler lifecycleHandler,
            ComponentDescriptor<T> componentDescriptor, String role, String roleHint) {
         super(container, lifecycleHandler, componentDescriptor, role, roleHint);
      }

      public synchronized void dispose() throws ComponentLifecycleException {
      }

      @SuppressWarnings("unchecked")
      public synchronized T getComponent() throws ComponentInstantiationException, ComponentLifecycleException {
         ComponentDescriptor<T> descriptor = getComponentDescriptor();
         Class<? extends T> enumClass = descriptor.getImplementationClass();

         if (!enumClass.isEnum()) {
            throw new ComponentInstantiationException(String.format("%s is not an emum class!", enumClass));
         }

         List<String> parts = Splitters.by(':').split(getRoleHint());
         String field = parts.get(0);
         Object[] values = Reflects.forMethod().invokeStaticMethod(enumClass, "values");

         for (Object value : values) {
            if (field.equals(value.toString())) {
               EnumValueHolder factory = new EnumValueHolder();

               try {
                  XBeanComponentBuilder<T> builder = new XBeanComponentBuilder<T>(this);
                  ObjectRecipe recipe = builder.createObjectRecipe((T) factory, descriptor, getRealm());

                  EnumValueHolder.put(value);
                  recipe.setFactoryMethod("get");
                  recipe.create(Object.class, false);

                  start(value);
               } catch (Exception e) {
                  throw new ComponentInstantiationException(e.getMessage(), e);
               } finally {
                  EnumValueHolder.reset();
               }

               return (T) value;
            }
         }

         throw new ComponentInstantiationException(String.format("Field(%s) is not defined in the %s!", field,
               enumClass));
      }

      public synchronized void release(Object component) throws ComponentLifecycleException {
      }
   }

   public static class EnumValueHolder {
      private static ThreadLocal<Object> m_threadLocal = new ThreadLocal<Object>();

      public static Object get() {
         return m_threadLocal.get();
      }

      public static void put(Object obj) {
         m_threadLocal.set(obj);
      }

      public static void reset() {
         m_threadLocal.remove();
      }
   }
}
