package org.unidal.lookup.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectUtils {
   @SuppressWarnings("unchecked")
   public static <T> T createInstance(Class<?> clazz) {
      try {
         if (clazz == null) {
            return null;
         } else {
            return (T) clazz.newInstance();
         }
      } catch (Exception e) {
         throw new RuntimeException("Error occured during creating instance of " + clazz, e);
      }
   }

   public static Object getField(Field field, Object instance) throws RuntimeException {
      try {
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }

         return field.get(instance);
      } catch (Exception e) {
         throw new RuntimeException("Error occured during getting field: " + field, e.getCause());
      }
   }

   public static Object invokeGetter(Object instance, String property) {
      Class<?> clazz = instance.getClass();

      Method getter;

      try {
         getter = clazz.getMethod("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
      } catch (Exception e) {
         throw new RuntimeException("No getter method found: " + e, e);
      }

      return invokeMethod(getter, instance);
   }

   public static Object invokeMethod(Method method, Object instance, Object... parameters) throws RuntimeException {
      try {
         return method.invoke(instance, parameters);
      } catch (Exception e) {
         throw new RuntimeException("Error occured during invoking method: " + method + " with parameters("
               + Arrays.asList(parameters) + ")", e.getCause());
      }
   }

   public static void setField(Field field, Object instance, Object value) throws RuntimeException {
      try {
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }

         field.set(instance, value);
      } catch (Exception e) {
         throw new RuntimeException("Error occured during setting field: " + field + " with value(" + value + ")", e
               .getCause());
      }
   }
}
