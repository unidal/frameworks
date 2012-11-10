package org.unidal.web.jsp.function;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import com.site.helper.Reflects;

public class ObjectHelper {
   public static Object length(Object obj) {
      if (obj == null) {
         return null;
      } else if (obj instanceof CharSequence) {
         return ((CharSequence) obj).length();
      } else if (obj.getClass().isArray()) {
         return Array.getLength(obj);
      } else {
         return Reflects.forMethod().invokeMethod(obj, "getLength", (Object[]) null);
      }
   }

   public static Object size(Object obj) {
      if (obj == null) {
         return null;
      } else if (obj instanceof Collection) {
         return ((Collection<?>) obj).size();
      } else if (obj instanceof Map) {
         return ((Map<?, ?>) obj).size();
      } else {
         return Reflects.forMethod().invokeMethod(obj, "getSize", (Object[]) null);
      }
   }
}
