package org.unidal.dal.jdbc.entity;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.lookup.annotation.Named;

@Named(type = DataObjectNaming.class)
public class DefaultDataObjectNaming implements DataObjectNaming {
   public Method getGetMethod(Class<? extends DataObject> clazz, String name) {
      Method method = null;

      try {
         method = clazz.getMethod(normalize("get", name, null));
      } catch (Exception e) {
         // ignore it
      }

      try {
         method = clazz.getMethod(normalize("is", name, null));
      } catch (Exception e) {
         // ignore it
      }

      if (method == null) {
         throw new DalRuntimeException("No Getter for DataField(" + name + ") defined at " + clazz);
      } else {
         return method;
      }
   }

   public Method getSetMethod(Class<? extends DataObject> clazz, String name) {
      String methodName = normalize("set", name, null);
      Method[] methods = clazz.getMethods();

      for (Method method : methods) {
         if (method.getName().equals(methodName)) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length == 1) {
               return method;
            }
         }
      }

      throw new DalRuntimeException("No Setter for DataField(" + name + ") defined at " + clazz);
   }

   protected Method getSetMethod(Class<? extends DataObject> clazz, String name, Object value) {
      Method method = getSetMethod(clazz, name);
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> type = typeToClass(parameterTypes[0]);

      if (value == null || isAssignable(value.getClass(), type)) {
         return method;
      }

      throw new DalRuntimeException("No Setter for DataField(" + name + ") defined at " + clazz);
   }

   protected boolean isAssignable(Class<?> from, Class<?> to) {
      if (from == to) {
         return true;
      } else if (to == Long.class && from == Integer.class) {
         return true;
      }

      return false;
   }

   protected Class<?> typeToClass(Class<?> type) {
      if (type == Boolean.TYPE) {
         return Boolean.class;
      } else if (type == Byte.TYPE) {
         return Byte.class;
      } else if (type == Character.TYPE) {
         return Character.class;
      } else if (type == Short.TYPE) {
         return Short.class;
      } else if (type == Integer.TYPE) {
         return Integer.class;
      } else if (type == Long.TYPE) {
         return Long.class;
      } else if (type == Float.TYPE) {
         return Float.class;
      } else if (type == Double.TYPE) {
         return Double.class;
      } else if (type == Void.TYPE) {
         return Void.class;
      } else if (type == Date.class) {
         return Timestamp.class;
      } else {
         return type;
      }
   }

   protected String normalize(String prefix, String rawFieldName, String suffix) {
      int len = (rawFieldName == null ? 0 : rawFieldName.length());
      StringBuilder sb = new StringBuilder(len);
      boolean firstChar = true;
      boolean hyphen = false;

      synchronized (sb) {
         if (prefix != null) {
            sb.append(prefix);
         }

         for (int i = 0; i < len; i++) {
            char ch = rawFieldName.charAt(i);

            if (ch == '-' || ch == '_')
               hyphen = true;
            else if (firstChar) {
               firstChar = false;
               sb.append(Character.toUpperCase(ch));
            } else if (hyphen) {
               hyphen = false;
               sb.append(Character.toUpperCase(ch));
            } else
               sb.append(ch);
         }

         if (suffix != null) {
            sb.append(suffix);
         }
      }

      return sb.toString();
   }
}
