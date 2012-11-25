package org.unidal.web.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.lookup.configuration.Configuration;
import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.annotation.ModulePagesMeta;
import org.unidal.web.mvc.model.ModuleRegistry;

public abstract class AbstractWebComponentsConfigurator extends AbstractResourceConfigurator {
   protected void defineInjectableComponent(List<Component> all, Class<?> clazz) {
      Component component = C(clazz);

      if (!isAutoConfigurable(clazz) || all.contains(component)) {
         return;
      }

      all.add(component);

      for (Field field : getInjectableFields(clazz)) {
         Class<?> type = field.getType();
         Inject inject = field.getAnnotation(Inject.class);

         if (inject != null) {
            Class<?> role = inject.type();
            String roleHint = inject.value();
            String is = inject.instantiationStrategy();
            String fieldName = null;

            if (role == Inject.Default.class) {
               role = type;
            } else {
               fieldName = field.getName();
            }

            if (is.length() > 0) {
               component.is(is);
            }

            if (roleHint.length() == 0) {
               component.req(role);
            } else if (fieldName == null) {
               component.req(role, roleHint);
            } else {
               component.req(role, roleHint, fieldName);
            }
         }

         if (!type.isArray() && !type.getName().startsWith("java")) {
            defineInjectableComponent(all, type);
         }
      }
   }

   protected void defineModule(List<Component> all, Class<?> moduleClass) {
      Component module = C(moduleClass);
      List<Class<?>> injectableClasses = new ArrayList<Class<?>>();
      ModulePagesMeta pagesMeta = moduleClass.getAnnotation(ModulePagesMeta.class);

      if (pagesMeta != null) {
         for (Class<?> handlerClass : pagesMeta.value()) {
            injectableClasses.add(handlerClass);
         }
      }

      for (Field field : getInjectableFields(moduleClass)) {
         Class<?> type = field.getType();
         Inject inject = field.getAnnotation(Inject.class);

         if (inject != null) {
            module.req(type);
            injectableClasses.add((Class<?>) type);
         }
      }

      all.add(module);

      for (Class<?> injectableClass : injectableClasses) {
         defineInjectableComponent(all, injectableClass);
      }
   }

   protected void defineModuleRegistry(List<Component> all, Class<? extends AbstractModule> defaultModuleClass,
         Class<? extends AbstractModule>... moduleClasses) {
      Configuration modules = E("modules");

      for (Class<?> moduleClass : moduleClasses) {
         if (moduleClass == defaultModuleClass) {
            modules.add(E("module", "default", "true").value(moduleClass.getName()));
         } else {
            modules.add(E("module").value(moduleClass.getName()));
         }
      }

      all.add(C(ModuleRegistry.class).config(modules));

      for (Class<?> moduleClass : moduleClasses) {
         defineModule(all, moduleClass);
      }
   }

   protected List<Field> getInjectableFields(Class<?> clazz) {
      List<Field> fields = Reflects.forField().getAllDeclaredFields(clazz, new IMemberFilter<Field>() {
         @Override
         public boolean filter(Field field) {
            return field.getAnnotation(Inject.class) != null;
         }
      });

      return fields;
   }

   protected boolean isAutoConfigurable(Class<?> clazz) {
      if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface() || clazz.isMemberClass()) {
         return false;
      } else {
         int modifiers = clazz.getModifiers();

         if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers)) {
            return false;
         }
      }

      return true;
   }
}