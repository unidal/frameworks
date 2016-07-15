package org.unidal.spring;

import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.unidal.lookup.ContainerLoader;
import org.unidal.tuple.Pair;

public class PlexusBeanFactory implements BeanFactory {
   @Override
   public boolean containsBean(String name) {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      return container.hasComponent(role, roleHint);
   }

   @Override
   public String[] getAliases(String name) {
      // no aliases supported in plexus
      return new String[0];
   }

   @Override
   public <T> T getBean(Class<T> requiredType) throws BeansException {
      PlexusContainer container = ContainerLoader.getDefaultContainer();
      List<ComponentDescriptor<?>> cds = container.getComponentDescriptorList(requiredType.getName());

      switch (cds.size()) {
      case 0:
         throw new NoSuchBeanDefinitionException(requiredType);
      case 1:
         try {
            List<T> list = container.lookupList(requiredType);

            return list.get(0);
         } catch (ComponentLookupException e) {
            throw new PlexusBeansException("Unable to get plexus component: " + e, e);
         }
      default:
         throw new NoUniqueBeanDefinitionException(requiredType);
      }
   }

   @Override
   public Object getBean(String name) throws BeansException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      if (container.hasComponent(role, roleHint)) {
         try {
            return container.lookup(role, roleHint);
         } catch (ComponentLookupException e) {
            throw new PlexusBeansException("Unable to get plexus component: " + e, e);
         }
      } else {
         throw new NoSuchBeanDefinitionException(name);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      if (container.hasComponent(role, roleHint)) {
         Object obj = null;

         try {
            obj = container.lookup(role, roleHint);

            return (T) obj;
         } catch (ComponentLookupException e) {
            throw new PlexusBeansException("Unable to get plexus component: " + e, e);
         } catch (ClassCastException e) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, obj.getClass());
         }
      } else {
         throw new NoSuchBeanDefinitionException(name);
      }
   }

   @Override
   public Object getBean(String name, Object... args) throws BeansException {
      if (args == null || args.length == 0) {
         return getBean(name);
      } else {
         throw new BeanDefinitionStoreException("Plexus component must use default constructor without any arguments!");
      }
   }

   @Override
   public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      if (container.hasComponent(role, roleHint)) {
         try {
            Object obj = container.lookup(role, roleHint);
            Class<? extends Object> type = obj.getClass();

            while (type != null) {
               if (type.getName().equals(role)) {
                  return type;
               }

               for (Class<?> iface : type.getInterfaces()) {
                  if (iface.getName().equals(role)) {
                     return iface;
                  }
               }

               type = type.getSuperclass();
            }

            return null;
         } catch (ComponentLookupException e) {
            throw new PlexusBeansException("Unable to get plexus component: " + e, e);
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();
      ComponentDescriptor<?> cd = container.getComponentDescriptor(role, roleHint);

      if (cd != null) {
         String is = cd.getInstantiationStrategy();

         return "PER_LOOKUP".equals(is);
      } else {
         throw new NoSuchBeanDefinitionException(name);
      }
   }

   @Override
   public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();
      ComponentDescriptor<?> cd = container.getComponentDescriptor(role, roleHint);

      if (cd != null) {
         String is = cd.getInstantiationStrategy();

         return !"PER_LOOKUP".equals(is);
      } else {
         throw new NoSuchBeanDefinitionException(name);
      }
   }

   @Override
   public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
      Pair<String, String> pair = parseName(name);
      String role = pair.getKey();
      String roleHint = pair.getValue();
      PlexusContainer container = ContainerLoader.getDefaultContainer();

      if (container.hasComponent(role, roleHint)) {
         try {
            Object obj = container.lookup(role, roleHint);

            return targetType.isAssignableFrom(obj.getClass());
         } catch (ComponentLookupException e) {
            throw new PlexusBeansException("Unable to get plexus component: " + e, e);
         }
      } else {
         throw new NoSuchBeanDefinitionException(name);
      }
   }

   private Pair<String, String> parseName(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Bean name can't be null!");
      }

      int pos = name.indexOf(':');

      if (pos < 0) {
         return new Pair<String, String>(name, "default");
      } else {
         return new Pair<String, String>(name.substring(0, pos), name.substring(pos + 1));
      }
   }
}
