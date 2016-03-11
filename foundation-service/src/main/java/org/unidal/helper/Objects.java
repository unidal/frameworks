package org.unidal.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
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

   public static XmlObject forXml() {
      return XmlObject.COMPACT;
   }

   public static JsonBuilder newJsonBuilder(int capacity) {
      return new JsonBuilder(new StringBuilder(capacity));
   }

   public static XmlBuilder newXmlBuilder(int capacity) {
      return new XmlBuilder(new StringBuilder(capacity));
   }

   public static class JsonBuilder {
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

      public JsonBuilder raw(char ch) {
         m_sb.append(ch);
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

      public JsonBuilder trimComma() {
         int len = m_sb.length();

         if (len > 0 && m_sb.charAt(len - 1) == ',') {
            m_sb.setLength(len - 1);
         }

         return this;
      }

      public JsonBuilder value(String value) {
         if (value == null) {
            m_sb.append("null");
         } else {
            String str = value;

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

   public enum JsonObject {
      COMPACT;

      public String from(Object obj) {
         JsonBuilder sb = new JsonBuilder(new StringBuilder(2048));
         Set<Object> done = new HashSet<Object>();

         fromObject(done, sb, obj);
         return sb.toString();
      }

      private void fromArray(Set<Object> done, JsonBuilder sb, Object obj) {
         int len = Array.getLength(obj);

         sb.raw('[');

         for (int i = 0; i < len; i++) {
            if (i > 0) {
               sb.comma();
            }

            Object element = Array.get(obj, i);

            fromObject(done, sb, element);
         }

         sb.raw(']');
      }

      @SuppressWarnings("unchecked")
      private void fromCollection(Set<Object> done, JsonBuilder sb, Object obj) {
         boolean first = true;

         sb.raw('[');

         for (Object item : ((Collection<Object>) obj)) {
            if (first) {
               first = false;
            } else {
               sb.comma();
            }

            fromObject(done, sb, item);
         }

         sb.raw(']');
      }

      @SuppressWarnings("unchecked")
      private void fromMap(Set<Object> done, JsonBuilder sb, Object obj) {
         boolean first = true;

         sb.raw('{');

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

         sb.raw('}');
      }

      private void fromObject(Set<Object> done, JsonBuilder sb, Object obj) {
         if (obj == null) {
            sb.raw("null");
         } else if (obj instanceof Formattable) {
            sb.raw(String.format("%#s", obj));
         } else if (obj instanceof Throwable) {
            Throwable t = (Throwable) obj;
            StringWriter writer = new StringWriter(1024);

            sb.raw('{');
            sb.key("name").colon().value(t.getClass().getName());

            if (t.getMessage() != null) {
               sb.raw(',');
               sb.key("message").colon().value(t.getMessage());
            }

            if (t.getStackTrace() != null) {
               sb.raw(',');
               t.printStackTrace(new PrintWriter(writer));
               sb.key("stackTrace").colon().value(writer.toString());
            }

            sb.raw('}');
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

   }

   public enum XmlObject {
      COMPACT;

      public String from(String name, Object obj) {
         XmlBuilder sb = new XmlBuilder(new StringBuilder(2048));
         Set<Object> done = new HashSet<Object>();

         fromObject(done, sb, name, obj);
         return sb.toString();
      }

      private void fromArray(Set<Object> done, XmlBuilder sb, String names, String name, Object obj) {
         int len = Array.getLength(obj);

         sb.tagStart(names);

         for (int i = 0; i < len; i++) {
            Object element = Array.get(obj, i);

            fromObject(done, sb, name, element);
         }

         sb.tagEnd(names);
      }

      @SuppressWarnings("unchecked")
      private void fromCollection(Set<Object> done, XmlBuilder sb, String names, String name, Object obj) {
         sb.tagStart(names);

         for (Object item : ((Collection<Object>) obj)) {
            fromObject(done, sb, name, item);
         }

         sb.tagEnd(names);
      }

      @SuppressWarnings("unchecked")
      private void fromMap(Set<Object> done, XmlBuilder sb, String names, String name, Object obj) {
         sb.tagStart(names);

         for (Map.Entry<Object, Object> e : ((Map<Object, Object>) obj).entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();

            sb.raw('<').raw(name).raw(" key=\"").value(String.valueOf(key)).raw("\">");
            fromObject(done, sb, null, value);
            sb.tagEnd(name).raw("\r\n");
         }

         sb.tagEnd(names);
      }

      private void fromObject(Set<Object> done, XmlBuilder sb, String name, Object obj) {
         if (obj == null) {
            sb.raw("<").raw(name).raw("/>");
         } else {
            if (obj instanceof Formattable) {
               sb.tagStart(name);
               sb.raw(String.format("%#s", obj));
               sb.tagEnd(name);
            } else if (obj instanceof Throwable) {
               Throwable t = (Throwable) obj;
               StringWriter writer = new StringWriter(1024);

               sb.tagStart(name);
               sb.tag("name", t.getClass().getName()).raw("\r\n");

               if (t.getMessage() != null) {
                  sb.tag("message", t.getMessage()).raw("\r\n");
               }

               if (t.getStackTrace() != null) {
                  t.printStackTrace(new PrintWriter(writer));
                  sb.tag("stackTrace", writer.toString()).raw("\r\n");
               }

               sb.tagEnd(name);
            } else {
               Class<?> type = obj.getClass();

               if (type == String.class || type == Class.class || type.isEnum()) {
                  sb.tagStart(name);
                  sb.value(obj.toString());
                  sb.tagEnd(name);
               } else if (type.isPrimitive() || Number.class.isAssignableFrom(type) || type == Boolean.class) {
                  sb.tagStart(name);
                  sb.raw(obj.toString());
                  sb.tagEnd(name);
               } else if (type == Date.class) {
                  sb.tagStart(name);
                  sb.value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj));
                  sb.tagEnd(name);
               } else if (done.contains(obj)) {
                  sb.tagStart(name);
                  sb.raw("<!-- ref -->");
                  sb.tagEnd(name);
               } else {
                  done.add(obj);

                  if (type.isArray()) {
                     fromArray(done, sb, null, name, obj);
                  } else if (Collection.class.isAssignableFrom(type)) {
                     fromCollection(done, sb, null, name, obj);
                  } else if (Map.class.isAssignableFrom(type)) {
                     fromMap(done, sb, null, name, obj);
                  } else {
                     fromPojo(done, sb, name, obj);
                  }
               }
            }
         }
      }

      private void fromPojo(Set<Object> done, XmlBuilder sb, String name, Object obj) {
         Class<? extends Object> type = obj.getClass();

         if (hasToXml(type)) {
            sb.tagStart(name);
            sb.raw((String) Reflects.forMethod().invokeDeclaredMethod(obj, "toXml"));
            sb.tagEnd(name);
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

         sb.tagStart(name);

         if (getters.isEmpty()) {
            // use java toString() since we can't handle it
            sb.value(obj.toString());
         } else {
            sb.raw("\r\n");

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

               fromObject(done, sb, key, value);
            }
         }

         sb.tagEnd(name);
      }

      private boolean hasToXml(Class<?> type) {
         try {
            Method method = type.getMethod("toXml");

            if (method.getParameterTypes().length == 0 && method.getReturnType() == String.class) {
               return true;
            }
         } catch (Exception e) {
            // ignore it
         }

         return false;
      }
   }

   public static class XmlBuilder {
      private StringBuilder m_sb;

      public XmlBuilder(StringBuilder sb) {
         m_sb = sb;
      }

      public XmlBuilder tag(String name, String value) {
         m_sb.append("<").append(name).append(">");
         value(value);
         m_sb.append("</").append(name).append(">");
         return this;
      }

      public XmlBuilder tagStart(String name) {
         if (name != null) {
            m_sb.append("<").append(name).append(">");
         }

         return this;
      }

      public XmlBuilder tagEnd(String name) {
         if (name != null) {
            m_sb.append("</").append(name).append(">\r\n");
         }

         return this;
      }

      public XmlBuilder raw(char ch) {
         m_sb.append(ch);
         return this;
      }

      public XmlBuilder raw(String rawString) {
         m_sb.append(rawString);
         return this;
      }

      @Override
      public String toString() {
         return m_sb.toString();
      }

      public XmlBuilder value(String value) {
         if (value == null) {
            m_sb.append("null");
         } else {
            String str = value;

            int len = str.length();

            for (int i = 0; i < len; i++) {
               char ch = str.charAt(i);

               switch (ch) {
               case '&':
                  m_sb.append("&amp;");
                  break;
               case '<':
                  m_sb.append("&lt;");
                  break;
               case '>':
                  m_sb.append("&gt;");
                  break;
               default:
                  m_sb.append(ch);
                  break;
               }
            }
         }

         return this;
      }
   }

}
