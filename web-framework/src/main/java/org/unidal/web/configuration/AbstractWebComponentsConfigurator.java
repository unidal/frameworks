package org.unidal.web.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Reflects;
import org.unidal.helper.Reflects.IMemberFilter;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.mvc.Module;
import org.unidal.web.mvc.annotation.ModulePagesMeta;
import org.unidal.web.mvc.model.ModuleRegistry;

public abstract class AbstractWebComponentsConfigurator extends AbstractResourceConfigurator {
   protected void defineInjectableComponent(List<Component> all, Class<?> clazz) {
      Component component = A(clazz);

      if (!shouldAutoConfigure(clazz) || all.contains(component)) {
         return;
      }

      all.add(component);

      List<Field> fields = getInjectableFields(clazz);
      Map<Class<?>, Integer> counts = getRoleCounts(fields);

      for (Field field : fields) {
         Inject inject = field.getAnnotation(Inject.class);
         Class<?> type = field.getType();
         Class<?> role;

         if (inject.type() == Inject.Default.class) {
            role = type;
         } else {
            role = inject.type();
         }

         String[] roleHints = inject.value();
         boolean needField = (counts.get(role).intValue() > 1);
         String fieldName = needField ? field.getName() : null;

         if (roleHints.length == 0) {
            component.req(role, "default", fieldName);
         } else if (roleHints.length == 1) {
            if (fieldName == null) {
               component.req(role, roleHints[0], fieldName);
            } else {
               component.req(role, roleHints[0], fieldName);
            }
         } else {
            component.req(role, roleHints, fieldName);
         }

         if (!type.isArray() && !type.getName().startsWith("java")) {
            defineInjectableComponent(all, type);
         }
      }
   }

   protected void defineModule(List<Component> all, Class<? extends Module> moduleClass) {
      Component module = A(moduleClass);
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

   protected void defineModuleRegistry(List<Component> all, Class<? extends Module> defaultModuleClass,
         Class<? extends Module>... moduleClasses) {
      if (defaultModuleClass != null) {
         all.add(A(ModuleRegistry.class).config(E("defaultModule").value(defaultModuleClass.getName())));
      } else {
         all.add(A(ModuleRegistry.class));
      }

      for (Class<? extends Module> moduleClass : moduleClasses) {
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

   private Map<Class<?>, Integer> getRoleCounts(List<Field> fields) {
      Map<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();

      for (Field field : fields) {
         Inject inject = field.getAnnotation(Inject.class);
         Class<?> role;

         if (inject.type() == Inject.Default.class) {
            role = field.getType();
         } else {
            role = inject.type();
         }

         Integer count = map.get(role);

         if (count == null) {
            count = 1;
         } else {
            count = count + 1;
         }

         map.put(role, count);
      }

      return map;
   }

   protected boolean shouldAutoConfigure(Class<?> clazz) {
      if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface() || clazz.isMemberClass()) {
         return false;
      } else {
         int modifiers = clazz.getModifiers();

         if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers)) {
            return false;
         } else if (clazz.isAnnotationPresent(Named.class)) {
            return false;
         }
      }

      return true;
   }
}