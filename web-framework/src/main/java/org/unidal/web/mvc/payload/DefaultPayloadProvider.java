package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.unidal.converter.ConverterManager;
import org.unidal.converter.TypeUtil;
import org.unidal.formatter.Formatter;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.util.ReflectUtils;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.Action;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.Page;
import org.unidal.web.mvc.PayloadProvider;
import org.unidal.web.mvc.model.entity.PayloadFieldModel;
import org.unidal.web.mvc.model.entity.PayloadModel;
import org.unidal.web.mvc.model.entity.PayloadObjectModel;
import org.unidal.web.mvc.model.entity.PayloadPathModel;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;
import org.unidal.web.mvc.payload.annotation.PathMeta;

@Named(type = PayloadProvider.class)
public class DefaultPayloadProvider extends ContainerHolder implements PayloadProvider<Page, Action>, LogEnabled {
   private Map<Class<?>, PayloadModel> m_payloadModels = new HashMap<Class<?>, PayloadModel>();

   private Map<Class<?>, Map<String, Method>> m_setters = new HashMap<Class<?>, Map<String, Method>>();

   private Logger m_logger;

   private Object convertValue(Type type, Object value, String format) {
      if (format == null) {
         return ConverterManager.getInstance().convert(value, type);
      } else {
         Class<?> clazz = TypeUtil.getRawType(type);
         Formatter<?> formatter = lookup(Formatter.class, clazz.getName());
         String text = value instanceof String ? (String) value : ((String[]) value)[0];

         try {
            return formatter.parse(format, text);
         } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
         } finally {
            release(formatter);
         }
      }
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   private List<Field> getDeclaredFields(Class<?> payloadClass) {
      List<Field> list = new ArrayList<Field>();
      Class<?> clazz = payloadClass;

      while (clazz != Object.class) {
         Field[] fields = clazz.getDeclaredFields();

         for (Field field : fields) {
            FieldMeta fieldMeta = field.getAnnotation(FieldMeta.class);
            PathMeta pathMeta = field.getAnnotation(PathMeta.class);
            ObjectMeta objectMeta = field.getAnnotation(ObjectMeta.class);

            if (fieldMeta != null || pathMeta != null || objectMeta != null) {
               list.add(field);
            }
         }

         clazz = clazz.getSuperclass();
      }

      return list;
   }

   private Method getSetMethod(Class<?> clazz, Field field) {
      String name;
      String fieldName = field.getName();

      if (fieldName.startsWith("m_") && fieldName.length() >= 3) {
         name = "set" + Character.toUpperCase(fieldName.charAt(2)) + fieldName.substring(3);
      } else {
         name = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
      }

      for (Method method : clazz.getMethods()) {
         if (method.getName().equals(name)) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1) {
               return method;
            }
         }
      }

      return null;
   }

   private void injectFieldValue(ActionPayload<?, ?> payload, PayloadFieldModel fieldModel, Object value) {
      Method method = fieldModel.getMethod();

      if (method != null) {
         Type parameterType = method.getGenericParameterTypes()[0];
         Object convertedValue = convertValue(parameterType, value, fieldModel.getFormat());

         ReflectUtils.invokeMethod(method, payload, convertedValue);
      } else {
         Field field = fieldModel.getField();
         Type parameterType = field.getGenericType();
         Object convertedValue = convertValue(parameterType, value, fieldModel.getFormat());

         ReflectUtils.setField(field, payload, convertedValue);
      }
   }

   private void injectObjectFields(PayloadObjectModel objectModel, Object instance, Map<String, String> map) {
      Class<?> type = instance.getClass();
      Method[] methods = type.getMethods();
      Map<String, Method> setters = m_setters.get(type);

      if (setters == null) {
         setters = new HashMap<String, Method>();

         m_setters.put(type, setters);
      }

      for (Map.Entry<String, String> e : map.entrySet()) {
         String property = e.getKey();

         if (property.length() > 0 && property.indexOf('.') < 0) {
            Method setter = setters.get(property);

            if (setter == null && !setters.containsKey(property)) {
               String setterName = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);

               for (Method method : methods) {
                  if (method.getName().equals(setterName)) {
                     setter = method;
                     break;
                  }
               }

               setters.put(property, setter);
            }

            if (setter != null) {
               Class<?>[] parameterTypes = setter.getParameterTypes();

               if (parameterTypes.length == 1) {
                  String value = e.getValue();
                  Object convertedValue = convertValue(parameterTypes[0], value, null);

                  ReflectUtils.invokeMethod(setter, instance, convertedValue);
               }
            }
         }
      }
   }

   private void injectPathValue(ActionPayload<?, ?> payload, PayloadPathModel pathModel, String[] parts) {
      Method method = pathModel.getMethod();

      if (method != null) {
         Type parameterType = method.getGenericParameterTypes()[0];
         Object convertedValue = convertValue(parameterType, parts, null);

         ReflectUtils.invokeMethod(method, payload, convertedValue);
      } else {
         Field field = pathModel.getField();
         Type parameterType = field.getGenericType();
         Object convertedValue = convertValue(parameterType, parts, null);

         ReflectUtils.setField(field, payload, convertedValue);
      }
   }

   private boolean isMultipleValues(Field field, Method method) {
      Class<?> valueType = (method != null ? method.getParameterTypes()[0] : field.getType());

      if (valueType.isArray()) {
         return true;
      } else if (Collection.class.isAssignableFrom(valueType)) {
         return true;
      }

      return false;
   }

   @SuppressWarnings("unchecked")
   public List<ErrorObject> process(UrlMapping mapping, ParameterProvider provider, ActionPayload<Page, Action> payload) {
      Class<?> payloadClass = payload.getClass();
      PayloadModel payloadModel = m_payloadModels.get(payloadClass);

      if (payloadModel == null) {
         m_logger.warn("Register " + payloadClass + " on demand");
         register((Class<? extends ActionPayload<? extends Page, ? extends Action>>) payloadClass);
         payloadModel = m_payloadModels.get(payloadClass);
      }

      List<ErrorObject> errors = new ArrayList<ErrorObject>();

      for (PayloadFieldModel field : payloadModel.getFields()) {
         try {
            processField(provider, payload, field);
         } catch (Exception e) {
            String name = field.getName();
            ErrorObject error = new ErrorObject("payload.field", e);

            error.addArgument(name, provider.getParameter(name));
            errors.add(error);
         }
      }

      for (PayloadObjectModel object : payloadModel.getObjects()) {
         try {
            processObject(provider, payload, object);
         } catch (Exception e) {
            String name = object.getName();
            ErrorObject error = new ErrorObject("payload.object", e);

            error.addArgument(name, provider.getParameter(name));
            errors.add(error);
         }
      }

      for (PayloadPathModel path : payloadModel.getPaths()) {
         try {
            processPath(mapping, payload, path);
         } catch (Exception e) {
            String name = path.getName();
            ErrorObject error = new ErrorObject("payload.path");

            error.addArgument(name, mapping.getPathInfo());
            errors.add(error);
         }
      }

      return errors;
   }

   private void processField(ParameterProvider provider, ActionPayload<?, ?> payload, PayloadFieldModel fieldModel)
         throws IOException {
      String name = fieldModel.getName();

      if (fieldModel.isRaw()) {
         InputStream value = provider.getRequest().getInputStream();

         if (value != null) {
            injectFieldValue(payload, fieldModel, value);
         }
      } else if (fieldModel.isMultipleValues()) {
         String[] values = provider.getParameterValues(name);

         if (values == null && fieldModel.getDefaultValue() != null) {
            values = fieldModel.getDefaultValue().split(",");
         }

         if (values == null) {
            values = new String[0];
         }

         if (values != null) {
            injectFieldValue(payload, fieldModel, values);
         }
      } else if (fieldModel.isFile()) {
         InputStream value = provider.getFile(name);

         if (value != null) {
            injectFieldValue(payload, fieldModel, value);
         }
      } else {
         String value = provider.getParameter(name);

         if (value == null) {
            value = fieldModel.getDefaultValue();
         }

         if (value != null) {
            injectFieldValue(payload, fieldModel, value);
         }
      }
   }

   private void processObject(ParameterProvider provider, ActionPayload<?, ?> payload, PayloadObjectModel objectModel)
         throws IOException {
      String name = objectModel.getName();
      String prefix = name + ".";
      int prefixLen = prefix.length();
      Map<String, String> map = new LinkedHashMap<String, String>();

      for (String parameterName : provider.getParameterNames()) {
         if (parameterName.startsWith(prefix)) {
            String value = provider.getParameter(parameterName);

            map.put(parameterName.substring(prefixLen), value);
         }
      }

      if (map.size() > 0) {
         Field field = objectModel.getField();
         Class<?> type = field.getType();
         Method method = objectModel.getMethod();

         if (type == Map.class) { // inject as a map
            Object instance = map;

            if (method != null) {
               ReflectUtils.invokeMethod(method, payload, instance);
            } else {
               ReflectUtils.setField(field, payload, instance);
            }
         } else { // inject as a pojo
            Object instance = ReflectUtils.createInstance(type);

            injectObjectFields(objectModel, instance, map);

            if (method != null) {
               ReflectUtils.invokeMethod(method, payload, instance);
            } else {
               ReflectUtils.setField(field, payload, instance);
            }
         }
      }
   }

   private void processPath(UrlMapping mapping, ActionPayload<?, ?> payload, PayloadPathModel pathModel)
         throws IOException {
      String pathInfo = mapping.getPathInfo(); // not starting with "/"
      String[] parts;

      if (pathInfo == null || pathInfo.length() == 0) {
         parts = new String[0];
      } else {
         parts = pathInfo.split(Pattern.quote("/"));
      }

      injectPathValue(payload, pathModel, parts);
   }

   public void register(Class<?> payloadClass) {
      PayloadModel payloadModel = new PayloadModel();

      for (Field field : getDeclaredFields(payloadClass)) {
         FieldMeta fieldMeta = field.getAnnotation(FieldMeta.class);
         PathMeta pathMeta = field.getAnnotation(PathMeta.class);
         ObjectMeta objectMeta = field.getAnnotation(ObjectMeta.class);
         int num = (fieldMeta != null ? 1 : 0) + (pathMeta != null ? 1 : 0) + (objectMeta != null ? 1 : 0);

         if (num > 1) {
            throw new RuntimeException(String.format("Field %s in %s can only be annotated by one of %s, %s or %s!",
                  field.getName(), payloadClass, FieldMeta.class.getSimpleName(), PathMeta.class.getSimpleName(),
                  ObjectMeta.class.getSimpleName()));
         }

         if (fieldMeta != null) {
            registerField(payloadClass, payloadModel, field, fieldMeta);
         } else if (pathMeta != null) {
            registerPath(payloadClass, payloadModel, field, pathMeta);
         } else if (objectMeta != null) {
            registerObject(payloadClass, payloadModel, field, objectMeta);
         }
      }

      payloadModel.setPayloadClass(payloadClass);
      m_payloadModels.put(payloadClass, payloadModel);
   }

   private void registerField(Class<?> payloadClass, PayloadModel payloadModel, Field field, FieldMeta fieldMeta) {
      PayloadFieldModel payloadFieldModel = new PayloadFieldModel();

      if (!fieldMeta.defaultValue().equals(FieldMeta.NOT_SPECIFIED)) {
         payloadFieldModel.setDefaultValue(fieldMeta.defaultValue());
      }

      payloadFieldModel.setName(fieldMeta.value());
      payloadFieldModel.setFormat(fieldMeta.format().length() == 0 ? null : fieldMeta.format());
      payloadFieldModel.setFile(fieldMeta.file());
      payloadFieldModel.setRaw(fieldMeta.raw());
      payloadFieldModel.setField(field);
      payloadFieldModel.setMethod(getSetMethod(payloadClass, field));
      payloadFieldModel.setMultipleValues(isMultipleValues(field, payloadFieldModel.getMethod()));

      if (payloadFieldModel.isFile()) {
         if (payloadFieldModel.isMultipleValues()) {
            throw new RuntimeException("Can't use file() and multiple values together!");
         } else if (payloadFieldModel.isRaw()) {
            throw new RuntimeException("Can't use file() and raw() together!");
         } else {
            Method m = payloadFieldModel.getMethod();
            Field f = payloadFieldModel.getField();

            if (m != null && m.getParameterTypes()[0] != InputStream.class) {
               throw new RuntimeException("Only InputStream is allowed as parameter type of " + m);
            } else if (f != null && f.getType() != InputStream.class) {
               throw new RuntimeException("Only InputStream is allowed as type of " + f);
            }
         }
      } else if (payloadFieldModel.isRaw()) {
         Method m = payloadFieldModel.getMethod();
         Field f = payloadFieldModel.getField();

         if (m != null && m.getParameterTypes()[0] != InputStream.class) {
            throw new RuntimeException("Only InputStream is allowed as parameter type of " + m);
         } else if (f != null && f.getType() != InputStream.class) {
            throw new RuntimeException("Only InputStream is allowed as type of " + f);
         }
      }

      payloadModel.addField(payloadFieldModel);
   }

   private void registerObject(Class<?> payloadClass, PayloadModel payloadModel, Field field, ObjectMeta objectMeta) {
      PayloadObjectModel payloadObjectModel = new PayloadObjectModel();

      payloadObjectModel.setName(objectMeta.value());
      payloadObjectModel.setField(field);
      payloadObjectModel.setMethod(getSetMethod(payloadClass, field));

      payloadModel.addObject(payloadObjectModel);
   }

   private void registerPath(Class<?> payloadClass, PayloadModel payloadModel, Field field, PathMeta pathMeta) {
      PayloadPathModel payloadPathModel = new PayloadPathModel();

      payloadPathModel.setName(pathMeta.value());
      payloadPathModel.setField(field);
      payloadPathModel.setMethod(getSetMethod(payloadClass, field));

      payloadModel.addPath(payloadPathModel);
   }
}
