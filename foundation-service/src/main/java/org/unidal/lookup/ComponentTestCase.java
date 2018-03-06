package org.unidal.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public abstract class ComponentTestCase extends ContainerHolder {
   private PlexusContainer m_container;

   private Class<?> getElementType(Field field) {
      Type type = field.getGenericType();
      Class<?> clazz = field.getType();

      if (clazz.isArray()) {
         return clazz.getComponentType();
      } else {
         if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();

            if (clazz == List.class || clazz == Set.class) {
               if (args.length == 1) {
                  return (Class<?>) args[0];
               }
            } else if (clazz == Map.class) {
               if (args.length == 2) {
                  return (Class<?>) args[1];
               }
            }
         }
      }

      return field.getType();
   }

   protected <T> ComponentDefinition<T> define(Class<T> implementation) throws Exception {
      Named named = implementation.getAnnotation(Named.class);

      if (named == null) {
         String name = implementation.getName();

         throw new IllegalArgumentException(String.format("Class(%s) should be annotated with @Named!", name));
      }

      String roleHint = (named.value().length() > 0 ? named.value() : null);

      return define(implementation, roleHint);
   }

   @SuppressWarnings("unchecked")
   protected <T> ComponentDefinition<T> define(Class<T> implementation, String roleHint) throws Exception {
      Named named = implementation.getAnnotation(Named.class);

      if (named == null) {
         String name = implementation.getName();

         throw new IllegalArgumentException(String.format("Class(%s) should be annotated with @Named!", name));
      }

      Class<T> role = (named.type() != Named.Default.class ? (Class<T>) named.type() : implementation);
      ComponentDefinition<T> component = defineComponent(role, roleHint, implementation);

      if (named.instantiationStrategy().length() > 0) {
         component.is(named.instantiationStrategy());
      } else if (implementation.isEnum()) {
         component.is(Named.ENUM);
      }

      defineDependencies(component, implementation);
      return component;
   }

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role) throws Exception {
      return defineComponent(role, null, role);
   }

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role, Class<? extends T> implementation)
         throws Exception {
      return defineComponent(role, null, implementation);
   }

   protected <T> ComponentDefinition<T> defineComponent(Class<T> role, String roleHint,
         Class<? extends T> implementation) throws Exception {
      ComponentModel model = new ComponentModel();

      model.setImplementation(implementation.getName());
      model.setRole(role.getName());

      if (roleHint != null) {
         model.setRoleHint(roleHint);
      }

      getContainer().addComponentModel(model);
      return new ComponentDefinition<T>(model);
   }

   private <T> void defineDependencies(ComponentDefinition<T> component, Class<T> implementation) {
      List<Field> fields = Reflects.forField().getAllDeclaredFields(implementation, new IMemberFilter<Field>() {
         @Override
         public boolean filter(Field member) {
            return member.isAnnotationPresent(Inject.class);
         }
      });

      for (Field field : fields) {
         defineDependency(component, field);
      }
   }

   private void defineDependency(ComponentDefinition<?> component, Field field) {
      Inject inject = field.getAnnotation(Inject.class);

      if (inject != null) {
         Class<?> role = inject.type();
         String[] roleHints = inject.value();
         Class<?> type = field.getType();
         Class<?> elementType = getElementType(field);

         if (role == Inject.Default.class) {
            role = elementType;
         }

         if (type == elementType) { // normal simple case
            if (roleHints.length == 0) {
               component.req(role);
            } else if (roleHints.length == 1) {
               component.req(role, roleHints[0]);
            } else {
               component.req(role, roleHints, field.getName());
            }
         } else { // List, Set or Array
            if (roleHints.length == 0) {
               component.req(role, null, field.getName());
            } else {
               component.req(role, roleHints, field.getName());
            }
         }
      }
   }

   @Override
   protected PlexusContainer getContainer() {
      return m_container;
   }

   @Before
   public void setUp() throws Exception {
      String configuration = getClass().getName().replace('.', '/') + ".xml";

      ContainerLoader.destroy();
      m_container = ContainerLoader.getDefaultContainer(configuration);
      System.setProperty("devMode", "true");
   }

   @After
   public void tearDown() throws Exception {
      ContainerLoader.destroy();
      Threads.reset();
      m_container = null;
   }

   protected static final class ComponentDefinition<T> {
      private ComponentModel m_model;

      public ComponentDefinition(ComponentModel descriptor) {
         m_model = descriptor;
      }

      public ComponentDefinition<T> config(String name, String value) {
         Any element = new Any().setName(name).setValue(value);
         ConfigurationModel config = m_model.getConfiguration();

         if (config == null) {
            config = new ConfigurationModel();
            m_model.setConfiguration(config);
         }

         config.getDynamicElements().add(element);
         return this;
      }

      public ComponentDefinition<T> is(String instantiationStrategy) {
         m_model.setInstantiationStrategy(instantiationStrategy);
         return this;
      }

      public ComponentDefinition<T> req(Class<?> role) {
         return req(role, null);
      }

      public ComponentDefinition<T> req(Class<?> role, String roleHint) {
         RequirementModel requirement = new RequirementModel();

         requirement.setRole(role.getName());

         if (roleHint != null) {
            requirement.setRoleHint(roleHint);
         }

         m_model.addRequirement(requirement);
         return this;
      }

      public ComponentDefinition<T> req(Class<?> role, String[] roleHints, String fieldName) {
         RequirementModel requirement = new RequirementModel();

         requirement.setRole(role.getName());
         requirement.setFieldName(fieldName);

         if (roleHints != null && roleHints.length > 0) {
            Any hints = new Any().setName("role-hints");

            requirement.getDynamicElements().add(hints);

            for (String roleHint : roleHints) {
               hints.addChild(new Any().setName("role-hint").setValue(roleHint));
            }
         }

         m_model.addRequirement(requirement);
         return this;
      }
   }
}