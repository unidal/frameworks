package org.unidal.lookup.container.lifecycle;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.converter.ConverterManager;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.RequirementModel;
import org.unidal.lookup.extension.Contextualizable;
import org.unidal.lookup.extension.Disposable;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.RoleHintEnabled;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.tuple.Pair;

public enum ComponentHandlers implements LifecycleHandler {
   REQUIREMENTS {
      @Override
      public void handleStart(LifecycleContext ctx) throws ComponentLookupException {
         ComponentModel model = ctx.getComponentModel();
         List<RequirementModel> requirements = model.getRequirements();
         RequirementInjector injector = new RequirementInjector();

         for (RequirementModel requirement : requirements) {
            injector.inject(ctx, requirement);
         }
      }
   },

   CONFIGURATION {
      @Override
      public void handleStart(LifecycleContext ctx) throws ComponentLookupException {
         ComponentModel model = ctx.getComponentModel();
         ConfigurationModel configuration = model.getConfiguration();

         if (configuration != null) {
            List<Any> elements = configuration.getDynamicElements();
            PropertyInjector injector = new PropertyInjector();

            for (Any element : elements) {
               String name = element.getName();
               String value = element.getValue();

               if (name != null && value != null) {
                  injector.injectProperty(ctx, name, value.trim());
               }
            }
         }
      }
   },

   CONTEXTUALIZABLE {
      @Override
      public void handleStart(final LifecycleContext ctx) throws ComponentLookupException {
         Object component = ctx.getComponent();

         if (component instanceof Contextualizable) {
            try {
               ((Contextualizable) component).contextualize(ctx.getContainer().getContext());
            } catch (Throwable e) {
               ComponentModel model = ctx.getComponentModel();

               throw new ComponentLookupException("Error when setting context of component!", model.getRole(),
                     model.getHint(), e);
            }
         }
      }
   },

   INITIALIZABLE {
      @Override
      public void handleStart(final LifecycleContext ctx) throws ComponentLookupException {
         Object component = ctx.getComponent();

         if (component instanceof Initializable) {
            try {
               ((Initializable) component).initialize();
            } catch (Throwable e) {
               ComponentModel model = ctx.getComponentModel();

               throw new ComponentLookupException("Error when initializing component!", model.getRole(),
                     model.getHint(), e);
            }
         }
      }
   },

   DISPOSABLE {
      @Override
      public void handleStop(final LifecycleContext ctx) {
         Object component = ctx.getComponent();

         if (component instanceof Disposable) {
            try {
               ((Disposable) component).dispose();
            } catch (Throwable e) {
               // ignore it
               e.printStackTrace();
            }
         }
      }
   },

   ENABLE_LOG {
      @Override
      public void handleStart(LifecycleContext ctx) {
         Object component = ctx.getComponent();

         if (component instanceof LogEnabled) {
            String role = ctx.getComponentModel().getRole();

            ((LogEnabled) component).enableLogging(ctx.getLogger(role));
         }
      }
   },

   ENABLE_ROLE_HINT {
      @Override
      public void handleStart(LifecycleContext ctx) {
         Object component = ctx.getComponent();

         if (component instanceof RoleHintEnabled) {
            ComponentModel model = ctx.getComponentModel();

            ((RoleHintEnabled) component).enableRoleHint(model.getHint());
         }
      }
   };

   @Override
   public void handleStart(LifecycleContext ctx) throws ComponentLookupException {
   }

   @Override
   public void handleStop(LifecycleContext ctx) {
   }

   private static class PropertyInjector {
      public void injectProperty(LifecycleContext ctx, String property, String value) throws ComponentLookupException {
         ComponentModel model = ctx.getComponentModel();
         Object component = ctx.getComponent();
         Method method = Reflects.forMethod().getSetterMethod(component, property);

         if (method == null) {
            String setter = Reflects.forMethod().getSetMethodName(property);
            String message = String.format("No setter method(%s) of class(%s) is found!", setter,
                  model.getImplementation());

            throw new ComponentLookupException(message, model.getRole(), model.getHint());
         } else {
            Class<?> type = method.getParameterTypes()[0];

            try {
               Object val = ConverterManager.getInstance().convert(value, type);

               method.invoke(component, val);
            } catch (Throwable e) {
               String setter = Reflects.forMethod().getSetMethodName(property);
               String message = String.format("No setter method(%s) of class(%s) is found!", setter,
                     model.getImplementation());

               throw new ComponentLookupException(message, model.getRole(), model.getHint(), e);
            }
         }
      }
   }

   private static class RequirementInjector {
      private Field findMatchedField(LifecycleContext ctx, RequirementModel requirement,
            Pair<String, List<String>> hints) throws ComponentLookupException {
         String role = requirement.getRole();

         if (hints.getValue().isEmpty()) { // single
            final Class<?> type = Reflects.forClass().getClass(role);
            Object component = ctx.getComponent();
            List<Field> fields = Reflects.forField().getDeclaredFields(component, new IMemberFilter<Field>() {
               @Override
               public boolean filter(Field field) {
                  if (field.isAnnotationPresent(Inject.class)) { // with @Inject
                     return type.isAssignableFrom(field.getType());
                  } else { // old code without @Inject
                     return type == field.getType();
                  }
               }
            });

            if (fields.size() == 1) {
               return fields.get(0);
            } else if (fields.size() == 0) {
               ComponentModel model = ctx.getComponentModel();
               String message = String.format("No field of class(%s) matches the type(%s)!", model.getImplementation(),
                     role);

               throw new ComponentLookupException(message, model.getRole(), model.getHint());
            } else {
               StringBuilder sb = new StringBuilder(32);

               for (Field field : fields) {
                  if (sb.length() > 0) {
                     sb.append(",");
                  }

                  sb.append(field.getName());
               }

               ComponentModel model = ctx.getComponentModel();
               String message = String.format("Multiple fields(%s) of class(%s) matches the type(%s)!", sb.toString(),
                     model.getImplementation(), requirement.getRole());

               throw new ComponentLookupException(message, model.getRole(), model.getHint());
            }
         } else { // multiple without fieldName
            ComponentModel model = ctx.getComponentModel();
            String message = String.format(
                  "For multiple role hints, fieldName must be specified in class(%s) to match the type(%s)!",
                  model.getImplementation(), role);

            throw new ComponentLookupException(message, model.getRole(), model.getHint());
         }
      }

      private Pair<String, List<String>> getHints(RequirementModel requirement) {
         String roleHint = requirement.getRoleHint();
         List<String> roleHints = new ArrayList<String>();

         for (Any element : requirement.getDynamicElements()) {
            if ("role-hints".equals(element.getName())) {
               for (Any any : element.getChildren()) {
                  roleHints.add(any.getValue());
               }
            }
         }

         return new Pair<String, List<String>>(roleHint, roleHints);
      }

      public void inject(LifecycleContext ctx, RequirementModel requirement) throws ComponentLookupException {
         String fieldName = requirement.getFieldName();
         Pair<String, List<String>> hints = getHints(requirement);
         Field field;

         if (fieldName != null && fieldName.length() > 0) {
            Object component = ctx.getComponent();

            field = Reflects.forField().getDeclaredField(component, fieldName);
         } else {
            field = findMatchedField(ctx, requirement, hints);
         }

         if (field != null) {
            injectField(ctx, requirement, field, hints);
         } else {
            String role = requirement.getRole();
            ComponentModel model = ctx.getComponentModel();
            String implementation = model.getImplementation();
            String message = String.format("No field of class(%s) matches the type(%s)!", implementation, role);

            throw new ComponentLookupException(message, model.getRole(), model.getHint());
         }
      }

      private void injectField(LifecycleContext ctx, RequirementModel requirement, Field field,
            Pair<String, List<String>> hints) throws ComponentLookupException {
         String role = requirement.getRole();
         Object component = ctx.getComponent();
         Class<?> type = field.getType();
         String roleHint = hints.getKey();
         List<String> roleHints = hints.getValue();
         Object value;

         if (type == List.class || type == Collection.class) { // List or Collection
            if (roleHints.isEmpty()) {
               value = new ArrayList<Object>(ctx.lookupList(role));
            } else {
               List<Object> dependencies = new ArrayList<Object>();

               for (String hint : roleHints) {
                  Object dependency = ctx.lookup(role, hint);

                  dependencies.add(dependency);
               }

               value = dependencies;
            }
         } else if (type.isArray()) { // Array
            if (roleHints.isEmpty()) {
               List<Object> list = ctx.lookupList(role);
               int size = list.size();
               Object dependencies = Array.newInstance(type.getComponentType(), size);
               int index = 0;

               for (Object item : list) {
                  Array.set(dependencies, index++, item);
               }

               value = dependencies;
            } else {
               int size = roleHints.size();
               Object dependencies = Array.newInstance(type.getComponentType(), size);

               for (int i = 0; i < size; i++) {
                  String hint = roleHints.get(i);
                  Object dependency = ctx.lookup(role, hint);

                  Array.set(dependencies, i, dependency);
               }

               value = dependencies;
            }
         } else if (type == Map.class) { // Map
            if (roleHints.isEmpty()) {
               value = ctx.lookupMap(role);
            } else {
               Map<String, Object> dependencies = new LinkedHashMap<String, Object>();

               for (String hint : roleHints) {
                  Object dependency = ctx.lookup(role, hint);

                  dependencies.put(hint, dependency);
               }

               value = dependencies;
            }
         } else if (type == Set.class) { // Set
            if (roleHints.isEmpty()) {
               value = new HashSet<Object>(ctx.lookupList(role));
            } else {
               Set<Object> dependencies = new HashSet<Object>();

               for (String hint : roleHints) {
                  Object dependency = ctx.lookup(role, hint);

                  dependencies.add(dependency);
               }

               value = dependencies;
            }
         } else if (roleHints.isEmpty()) { // single
            value = ctx.lookup(role, roleHint);
         } else {
            ComponentModel model = ctx.getComponentModel();
            String implementation = model.getImplementation();
            String message = String.format("Unknown type(%s) of field(%s) of class(%s)!", type.getName(),
                  field.getName(), implementation);

            throw new ComponentLookupException(message, model.getRole(), model.getHint());
         }

         try {
            field.setAccessible(true);
            field.set(component, value);
         } catch (Throwable e) {
            ComponentModel model = ctx.getComponentModel();
            String implementation = model.getImplementation();
            String message = String.format("Unable to inject field(%s) of class(%s) with instance of %s!",
                  field.getName(), implementation, value.getClass());

            throw new ComponentLookupException(message, model.getRole(), model.getHint(), e);
         }
      }
   }
}
