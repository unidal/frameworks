package org.unidal.web.jsp.function;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Reflects;
import org.unidal.web.jsp.annotation.FunctionMeta;

public class ObjectFunction {
   @FunctionMeta(description = "Length of String, or array", example = "${w:length(obj)}")
   public static Object length(Object obj) {
      if (obj == null) {
         return null;
      } else if (obj instanceof CharSequence) {
         return ((CharSequence) obj).length();
      } else if (obj.getClass().isArray()) {
         return Array.getLength(obj);
      } else {
         try {
            return Reflects.forMethod().invokeMethod(obj, "getLength", (Object[]) null);
         } catch (Exception e) {
            return null;
         }
      }
   }

   @FunctionMeta(description = "size of colection, or map", example = "${w:size(obj)}")
   public static Object size(Object obj) {
      if (obj == null) {
         return null;
      } else if (obj instanceof Collection) {
         return ((Collection<?>) obj).size();
      } else if (obj instanceof Map) {
         return ((Map<?, ?>) obj).size();
      } else {
         try {
            return Reflects.forMethod().invokeMethod(obj, "getSize", (Object[]) null);
         } catch (Exception e) {
            return null;
         }
      }
   }

   @FunctionMeta(description = "Check if the value is equal or is one of value in the given values.", example = "${w:in(values, value)}")
   @SuppressWarnings("unchecked")
   public static boolean in(Object values, Object value) {
      if (values == null || value == null) {
         return false;
      }

      if (values instanceof List) {
         for (Object v : (List<Object>) values) {
            if (v != null && value.toString().equals(v.toString())) {
               return true;
            }
         }

         return false;
      } else if (values.getClass().isArray()) {
         int len = Array.getLength(values);

         for (int i = 0; i < len; i++) {
            Object v = Array.get(values, i);

            if (v != null && value.toString().equals(v.toString())) {
               return true;
            }
         }

         return false;
      } else {
         return values.toString().equals(value.toString());
      }
   }
}
