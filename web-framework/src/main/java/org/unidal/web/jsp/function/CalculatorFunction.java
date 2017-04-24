package org.unidal.web.jsp.function;

import java.lang.reflect.Method;
import java.util.List;

import org.unidal.web.jsp.annotation.FunctionMeta;

public class CalculatorFunction {
   private static Method getGetter(String fieldName, Class<?> clazz) {
      try {
         return clazz.getMethod("get" + capitalizeFirstLetter(fieldName));
      } catch (Exception e) {
         // ignore it
      }

      try {
         return clazz.getMethod("is" + capitalizeFirstLetter(fieldName));
      } catch (Exception e) {
         // ignore it
      }

      throw new IllegalArgumentException("No getter method for " + fieldName + " in " + clazz);
   }

   private static String capitalizeFirstLetter(String str) {
      int len = str.length();
      StringBuilder sb = new StringBuilder(len);

      sb.append(str.charAt(0));
      sb.append(str.substring(1));
      return sb.toString();
   }

   @FunctionMeta(description = "Max value of field value of list elements", example = "${w:max(list, 'count')}")
   public static int max(List<?> list, String fieldName) {
      int max = Integer.MIN_VALUE;

      if (list != null && list.size() > 0) {
         Method method = getGetter(fieldName, list.get(0).getClass());

         if (method.getReturnType().isPrimitive() || Number.class.isAssignableFrom(method.getReturnType())) {
            for (Object item : list) {
               try {
                  Object value = method.invoke(item, new Object[0]);
                  int val = 0;

                  if (value instanceof Boolean) {
                     val = ((Boolean) value).booleanValue() ? 1 : 0;
                  } else if (value instanceof Number) {
                     val = ((Number) value).intValue();
                  }

                  if (val > max) {
                     max = val;
                  }
               } catch (Exception e) {
                  // ignore it
               }
            }
         }
      }

      if (max == Integer.MIN_VALUE) {
         return 0;
      } else {
         return max;
      }
   }

   @FunctionMeta(description = "Min value of field value of list elements", example = "${w:min(list, 'count')}")
   public static int min(List<?> list, String fieldName) {
      int min = Integer.MAX_VALUE;

      if (list != null && list.size() > 0) {
         Method method = getGetter(fieldName, list.get(0).getClass());

         if (method.getReturnType().isPrimitive() || Number.class.isAssignableFrom(method.getReturnType())) {
            for (Object item : list) {
               try {
                  Object value = method.invoke(item, new Object[0]);
                  int val = 0;

                  if (value instanceof Boolean) {
                     val = ((Boolean) value).booleanValue() ? 1 : 0;
                  } else if (value instanceof Number) {
                     val = ((Number) value).intValue();
                  }

                  if (val < min) {
                     min = val;
                  }
               } catch (Exception e) {
                  // ignore it
               }
            }
         }
      }

      if (min == Integer.MAX_VALUE) {
         return 0;
      } else {
         return min;
      }
   }

   @FunctionMeta(description = "Sum of field value of list elements", example = "${w:sum(list, 'amount')}")
   public static double sum(List<?> list, String fieldName) {
      double sum = 0;

      if (list != null && list.size() > 0) {
         Method method = getGetter(fieldName, list.get(0).getClass());

         if (method.getReturnType().isPrimitive() || Number.class.isAssignableFrom(method.getReturnType())) {
            for (Object item : list) {
               try {
                  Object value = method.invoke(item, new Object[0]);

                  if (value instanceof Boolean) {
                     sum += ((Boolean) value).booleanValue() ? 1 : 0;
                  } else if (value instanceof Number) {
                     sum += ((Number) value).doubleValue();
                  }
               } catch (Exception e) {
                  // ignore it
               }
            }
         }
      }

      return sum;
   }
}
