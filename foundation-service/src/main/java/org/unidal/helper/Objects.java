package org.unidal.helper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formattable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.helper.Reflects.IMemberFilter;

public class Objects {
   public static JsonObject forJson() {
      return JsonObject.COMPACT;
   }

   public static enum JsonObject {
      COMPACT;

      public String from(Object obj) {
         JsonBuilder sb = new JsonBuilder(new StringBuilder(2048));
         Set<Object> done = new HashSet<Object>();

         fromObject(done, sb, obj);
         return sb.toString();
      }

      private void fromArray(Set<Object> done, JsonBuilder sb, Object obj) {
         int len = Array.getLength(obj);

         sb.raw("[");

         for (int i = 0; i < len; i++) {
            if (i > 0) {
               sb.comma();
            }

            Object element = Array.get(obj, i);

            fromObject(done, sb, element);
         }

         sb.raw("]");
      }

      @SuppressWarnings("unchecked")
      private void fromCollection(Set<Object> done, JsonBuilder sb, Object obj) {
         boolean first = true;

         sb.raw("[");

         for (Object item : ((Collection<Object>) obj)) {
            if (first) {
               first = false;
            } else {
               sb.comma();
            }

            fromObject(done, sb, item);
         }

         sb.raw("]");
      }

      @SuppressWarnings("unchecked")
      private void fromMap(Set<Object> done, JsonBuilder sb, Object obj) {
         boolean first = true;

         sb.raw("{");

         for (Map.Entry<Object, Object> e : ((Map<Object, Object>) obj).entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();

            if (first) {
               first = false;
            } else {
               sb.comma();
            }

            sb.key(String.valueOf(key)).colon();
            fromObject(done, sb, value);
         }

         sb.raw("}");
      }

      private void fromObject(Set<Object> done, JsonBuilder sb, Object obj) {
         if (obj == null) {
            sb.raw("null");
         } else if (obj instanceof Formattable) {
            sb.raw(String.format("%#s", obj));
         } else {
            Class<?> type = obj.getClass();

            if (type == String.class || type == Class.class || type.isEnum()) {
               sb.value(obj.toString());
            } else if (type.isPrimitive() || Number.class.isAssignableFrom(type) || type == Boolean.class) {
               sb.raw(obj.toString());
            } else if (type == Date.class) {
               sb.value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj));
            } else if (done.contains(obj)) {
               sb.raw("{}");
            } else {
               done.add(obj);

               if (type.isArray()) {
                  fromArray(done, sb, obj);
               } else if (Collection.class.isAssignableFrom(type)) {
                  fromCollection(done, sb, obj);
               } else if (Map.class.isAssignableFrom(type)) {
                  fromMap(done, sb, obj);
               } else {
                  fromPojo(done, sb, obj);
               }
            }
         }
      }

      private void fromPojo(Set<Object> done, JsonBuilder sb, Object obj) {
         Class<? extends Object> type = obj.getClass();

         if (hasToString(type)) {
            fromObject(done, sb, obj.toString());
            return;
         }

         List<Method> getters = Reflects.forMethod().getMethods(type, new IMemberFilter<Method>() {
            @Override
            public boolean filter(Method method) {
               return Reflects.forMethod().isGetter(method);
            }
         });

         Collections.sort(getters, new Comparator<Method>() {
            @Override
            public int compare(Method m1, Method m2) {
               return m1.getName().compareTo(m2.getName());
            }
         });

         if (getters.isEmpty()) {
            // use java toString() since we can't handle it
            sb.value(obj.toString());
         } else {
            boolean first = true;

            sb.raw("{");

            for (Method getter : getters) {
               String key = Reflects.forMethod().getGetterName(getter);
               Object value;

               try {
                  if (!getter.isAccessible()) {
                     getter.setAccessible(true);
                  }

                  value = getter.invoke(obj);
               } catch (Exception e) {
                  // ignore it
                  value = null;
               }

               if (value == null) {
                  continue;
               }

               if (first) {
                  first = false;
               } else {
                  sb.comma();
               }

               sb.key(key).colon();
               fromObject(done, sb, value);
            }

            sb.raw("}");
         }
      }

      public boolean hasToString(Class<?> type) {
         try {
            Method method = type.getMethod("toString");

            if (method.getDeclaringClass() != Object.class) {
               return true;
            }
         } catch (Exception e) {
            // ignore it
         }

         return false;
      }

      static class JsonBuilder {
         private StringBuilder m_sb;

         public JsonBuilder(StringBuilder sb) {
            m_sb = sb;
         }

         public JsonBuilder colon() {
            m_sb.append(':');
            return this;
         }

         public JsonBuilder comma() {
            m_sb.append(',');
            return this;
         }

         public JsonBuilder key(String key) {
            m_sb.append('"').append(key).append('"');
            return this;
         }

         public JsonBuilder raw(String rawString) {
            m_sb.append(rawString);
            return this;
         }

         @Override
         public String toString() {
            return m_sb.toString();
         }

         public JsonBuilder value(String value) {
            if (value == null) {
               m_sb.append("null");
            } else {
               String str = value.toString();

               int len = str.length();

               m_sb.append('"');

               for (int i = 0; i < len; i++) {
                  char ch = str.charAt(i);

                  switch (ch) {
                  case '\t':
                     m_sb.append('\\').append('t');
                     break;
                  case '\r':
                     m_sb.append('\\').append('r');
                     break;
                  case '\n':
                     m_sb.append('\\').append('n');
                     break;
                  case '\\':
                  case '"':
                     m_sb.append('\\').append(ch);
                     break;
                  default:
                     m_sb.append(ch);
                     break;
                  }
               }

               m_sb.append('"');
            }

            return this;
         }
      }
   }
}
