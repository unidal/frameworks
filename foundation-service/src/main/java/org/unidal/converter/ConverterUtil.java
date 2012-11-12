package org.unidal.converter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConverterUtil {
   private static Map<Class<?>, Map<String, Method>> s_cachedSetMethodMap = new HashMap<Class<?>, Map<String, Method>>();

   public static String getSetMethodName(String name) {
      StringBuilder sb = new StringBuilder(32);
      int len = name.length();
      boolean uppercase = true;

      sb.append("set");

      for (int i = 0; i < len; i++) {
         char ch = name.charAt(i);

         switch (ch) {
         case '-':
         case '_':
            uppercase = true;
            break;
         default:
            if (uppercase) {
               sb.append(Character.toUpperCase(ch));
               uppercase = false;
            } else {
               sb.append(ch);
            }
         }
      }

      return sb.toString();
   }

   public static Method getSetMethod(Class<?> clazz, String methodName) {
      Map<String, Method> map = s_cachedSetMethodMap.get(clazz);

      if (map == null) {
         synchronized (s_cachedSetMethodMap) {
            map = s_cachedSetMethodMap.get(clazz);

            if (map == null) {
               map = new HashMap<String, Method>();
               s_cachedSetMethodMap.put(clazz, map);
            }
         }
      }

      Method method = map.get(methodName);

      if (method == null) {
         synchronized (map) {
            method = map.get(methodName);

            if (method == null) {
               Method[] methods = clazz.getMethods();

               for (Method e : methods) {
                  if (e.getName().equalsIgnoreCase(methodName) && e.getParameterTypes().length == 1) {
                     method = e;
                     break;
                  }
               }

               if (method != null) {
                  map.put(methodName, method);
               }
            }
         }
      }

      if (method == null) {
         throw new RuntimeException("Can't find set method(" + methodName + ") in " + clazz);
      } else {
         return method;
      }
   }
}
