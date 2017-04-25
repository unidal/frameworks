package org.unidal.lookup.container.lifecycle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.unidal.converter.ConverterManager;
import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.RequirementModel;
import org.unidal.lookup.extension.RoleHintEnabled;

public enum ComponentHandlers implements LifecycleHandler {
   REQUIREMENTS {
      @Override
      public void handleStart(LifecycleContext ctx) throws ComponentLookupException {
         ComponentModel model = ctx.getComponentModel();
         List<RequirementModel> requirements = model.getRequirements();

         for (RequirementModel requirement : requirements) {
            if (requirement.getDynamicElements().isEmpty()) {
               Object dependency = ctx.lookup(requirement.getRole(), requirement.getRoleHint());
               String fieldName = requirement.getFieldName();

               if (fieldName != null && fieldName.length() > 0) {
                  injectRequirementByField(ctx, dependency, requirement);
               } else {
                  injectRequirementByType(ctx, dependency, requirement);
               }
            } else {
               List<Any> elements = requirement.getDynamicElements();
               List<Object> dependencies = new ArrayList<Object>();

               for (Any element : elements) {
                  if ("role-hints".equals(element.getName())) {
                     for (Any any : element.getChildren()) {
                        Object dependency = ctx.lookup(requirement.getRole(), any.getValue());

                        dependencies.add(dependency);
                     }
                  }
               }

               injectRequirementsByField(ctx, dependencies, requirement);
            }
         }
      }

      private void injectRequirementByField(LifecycleContext ctx, Object dependency, RequirementModel requirement)
            throws ComponentLookupException {
         Object component = ctx.getComponent();
         String fieldName = requirement.getFieldName();
         Field field = Reflects.forField().getDeclaredField(component, fieldName);

         if (field != null) {
            try {
               field.setAccessible(true);
               field.set(component, dependency);
            } catch (Throwable e) {
               ComponentModel model = ctx.getComponentModel();
               String message = String.format("Unable to set field(%s) in class(%s)!", fieldName,
                     model.getImplementation());

               throw new ComponentLookupException(message, model.getRole(), model.getRoleHint(), e);
            }
         } else {
            ComponentModel model = ctx.getComponentModel();
            String message = String.format("No field(%s) is found in class(%s)!", fieldName, model.getImplementation());

            throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
         }
      }

      private void injectRequirementByType(LifecycleContext ctx, final Object dependency, RequirementModel requirement)
            throws ComponentLookupException {
         Object component = ctx.getComponent();
         List<Field> fields = Reflects.forField().getDeclaredFields(component, new IMemberFilter<Field>() {
            @Override
            public boolean filter(Field field) {
               return field.getType().isAssignableFrom(dependency.getClass());
            }
         });

         ComponentModel model = ctx.getComponentModel();
         int len = fields.size();

         if (len == 0) {
            String message = String.format("No field of class(%s) matches the type(%s)!", model.getImplementation(),
                  requirement.getRole());

            throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
         } else if (len == 1) {
            Field field = fields.get(0);

            try {
               field.setAccessible(true);
               field.set(component, dependency);
            } catch (Throwable e) {
               String message = String.format("Unable to set field(%s) of class(%s) with the type(%s)!",
                     field.getName(), model.getImplementation(), requirement.getRole());

               throw new ComponentLookupException(message, model.getRole(), model.getRoleHint(), e);
            }
         } else {
            StringBuilder sb = new StringBuilder();

            for (Field field : fields) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(field.getName());
            }

            String message = String.format("Multiple fields(%s) of class(%s) matches the type(%s)!", sb.toString(),
                  model.getImplementation(), requirement.getRole());

            throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
         }
      }

      private void injectRequirementsByField(LifecycleContext ctx, List<Object> dependencies,
            RequirementModel requirement) throws ComponentLookupException {
         Object component = ctx.getComponent();
         String fieldName = requirement.getFieldName();
         Field field = Reflects.forField().getDeclaredField(component, fieldName);

         if (field != null) {
            try {
               field.setAccessible(true);
               field.set(component, dependencies);
            } catch (Throwable e) {
               ComponentModel model = ctx.getComponentModel();
               String message = String.format("Unable to set field(%s) in class(%s)!", fieldName,
                     model.getImplementation());

               throw new ComponentLookupException(message, model.getRole(), model.getRoleHint(), e);
            }
         } else {
            ComponentModel model = ctx.getComponentModel();
            String message = String.format("No field(%s) is found in class(%s)!", fieldName, model.getImplementation());

            throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
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

            for (Any element : elements) {
               String name = element.getName();
               String value = element.getValue();

               if (name != null && value != null) {
                  injectProperty(ctx, name, value.trim());
               }
            }
         }
      }

      private void injectProperty(LifecycleContext ctx, String property, String value) throws ComponentLookupException {
         ComponentModel model = ctx.getComponentModel();
         Object component = ctx.getComponent();
         Method method = Reflects.forMethod().getSetterMethod(component, property);

         if (method == null) {
            String setter = Reflects.forMethod().getSetMethodName(property);
            String message = String.format("No setter method(%s) of class(%s) is found!", setter,
                  model.getImplementation());

            throw new ComponentLookupException(message, model.getRole(), model.getRoleHint());
         } else {
            Class<?> type = method.getParameterTypes()[0];

            try {
               Object val = ConverterManager.getInstance().convert(value, type);

               method.invoke(component, val);
            } catch (Throwable e) {
               String setter = Reflects.forMethod().getSetMethodName(property);
               String message = String.format("No setter method(%s) of class(%s) is found!", setter,
                     model.getImplementation());

               throw new ComponentLookupException(message, model.getRole(), model.getRoleHint(), e);
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
                     model.getRoleHint(), e);
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
                     model.getRoleHint(), e);
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

            ((RoleHintEnabled) component).enableRoleHint(model.getRoleHint());
         }
      }
   };

   @Override
   public void handleStart(LifecycleContext ctx) throws ComponentLookupException {
   }

   @Override
   public void handleStop(LifecycleContext ctx) {
   }
}
