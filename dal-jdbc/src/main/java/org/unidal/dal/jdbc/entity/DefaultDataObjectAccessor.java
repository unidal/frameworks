package org.unidal.dal.jdbc.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = DataObjectAccessor.class)
public class DefaultDataObjectAccessor implements DataObjectAccessor, LogEnabled {
   @Inject
   private DataObjectNaming m_naming;

   private Map<Class<? extends DataObject>, Map<String, Method>> m_getMap = new HashMap<Class<? extends DataObject>, Map<String, Method>>();

   private Map<Class<? extends DataObject>, Map<String, Method>> m_setMap = new HashMap<Class<? extends DataObject>, Map<String, Method>>();

   private Logger m_logger;

   protected Object convert(Object value, Class<?> clazz) {
      if (value == null) {
         return null;
      } else if (value.getClass() == clazz) {
         return value;
      }

      if (clazz == Integer.class || clazz == Integer.TYPE) {
         if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
         } else {
            return Integer.valueOf(value.toString());
         }
      } else if (clazz == Long.class || clazz == Long.TYPE) {
         if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
         } else {
            return Long.valueOf(value.toString());
         }
      } else if (clazz == Double.class || clazz == Double.TYPE) {
         if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
         } else {
            return Double.valueOf(value.toString());
         }
      } else if (clazz == Float.class || clazz == Float.TYPE) {
         if (value instanceof Number) {
            return Float.valueOf(((Number) value).floatValue());
         } else {
            return Float.valueOf(value.toString());
         }
      } else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
         String val = value.toString();

         if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("1") || val.equalsIgnoreCase("on")
               || val.equalsIgnoreCase("T") || val.equalsIgnoreCase("Y")) {
            return Boolean.TRUE;
         } else {
            return Boolean.FALSE;
         }
      } else if (clazz == Date.class) {
         if (value instanceof Date) { // for java.sql.Date
            return value;
         } else if (value instanceof Timestamp) {
            return value;
         }
      } else if (clazz == byte[].class) {
         if (value instanceof Blob) {
            Blob blob = (Blob) value;

            try {
               return blob.getBytes(0L, (int) blob.length());
            } catch (Exception e) {
               // ignore it and pass through
            }
         }
      } else if (clazz.isAssignableFrom(InputStream.class)) {
         if (value.getClass() == byte[].class) {
            return new ByteArrayInputStream((byte[]) value);
         }
      }

      m_logger.error("Can't convert type from " + value.getClass() + " to " + clazz + " at " + getClass());

      return value;
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public Object getFieldValue(DataObject dataObject, DataField dataField) {
      if (dataObject instanceof RawDataObject) {
         return ((RawDataObject) dataObject).getFieldValue(dataField.getName());
      }

      Class<? extends DataObject> clazz = dataObject.getClass();
      Map<String, Method> methods = m_getMap.get(clazz);

      if (methods == null) {
         methods = new HashMap<String, Method>();
         m_getMap.put(clazz, methods);
      }

      String name = dataField.getName();
      Method method = methods.get(name);

      if (method == null) {
         method = m_naming.getGetMethod(clazz, name);
         methods.put(name, method);
      }

      try {
         Object value = method.invoke(dataObject);

         return value;
      } catch (Exception e) {
         throw new DalRuntimeException("Error when getting value of field(" + name + ") of " + clazz, e);
      }
   }

   public <T extends DataObject> T newInstance(Class<T> clazz) {
      try {
         return clazz.getConstructor().newInstance();
      } catch (Exception e) {
         throw new DalRuntimeException("Error when creating new instance of " + clazz, e);
      }
   }

   public void setFieldValue(DataObject dataObject, DataField dataField, Object value) {
      if (dataObject instanceof RawDataObject) {
         ((RawDataObject) dataObject).setFieldUsed(dataField, true);
         ((RawDataObject) dataObject).setFieldValue(dataField.getName(), value);
         return;
      }

      Class<? extends DataObject> clazz = dataObject.getClass();
      Map<String, Method> methods = m_setMap.get(clazz);

      if (methods == null) {
         methods = new HashMap<String, Method>();
         m_setMap.put(clazz, methods);
      }

      String name = dataField.getName();
      Method method = methods.get(name);

      if (method == null) {
         method = m_naming.getSetMethod(clazz, name);
         methods.put(name, method);
      }

      Class<?> type = method.getParameterTypes()[0];
      Object newValue = convert(value, type);

      try {
         if (newValue != null) {
            method.invoke(dataObject, newValue);
         }
      } catch (Exception e) {
         throw new DalRuntimeException("Error when setting value of field(" + name + ") of " + clazz + ", required: "
               + type + ", but: " + value.getClass(), e);
      }
   }
}
