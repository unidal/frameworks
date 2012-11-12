package org.unidal.converter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeUtil {
   private static Map<Class<?>, Class<?>> m_primitiveTypeMap = new HashMap<Class<?>, Class<?>>();

   static {
      m_primitiveTypeMap.put(Boolean.TYPE, Boolean.class);
      m_primitiveTypeMap.put(Byte.TYPE, Byte.class);
      m_primitiveTypeMap.put(Character.TYPE, Character.class);
      m_primitiveTypeMap.put(Short.TYPE, Short.class);
      m_primitiveTypeMap.put(Integer.TYPE, Integer.class);
      m_primitiveTypeMap.put(Long.TYPE, Long.class);
      m_primitiveTypeMap.put(Float.TYPE, Float.class);
      m_primitiveTypeMap.put(Double.TYPE, Double.class);
   }

   public static Type getComponentType(Type type) {
      Class<?> clazz;

      if (type instanceof Class) {
         clazz = (Class<?>) type;

         if (clazz.isArray()) {
            return clazz.getComponentType();
         }
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType) type;
         Type[] actualTypes = parameterizedType.getActualTypeArguments();

         if (actualTypes.length == 1) {
            return actualTypes[0];
         } else {
            return actualTypes[1];
         }
      } else if (type instanceof GenericArrayType) {
         GenericArrayType genericArrayType = (GenericArrayType) type;

         return genericArrayType.getGenericComponentType();
      } else {
         throw new ConverterException("Unknown type: " + type);
      }

      return Object.class;
   }

   public static Class<?> getRawType(Type type) {
      Class<?> clazz;

      if (type instanceof Class) {
         clazz = (Class<?>) type;
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         clazz = (Class<?>) parameterizedType.getRawType();
      } else if (type instanceof TypeVariable) {
         TypeVariable<?> typeVariable = (TypeVariable<?>) type;

         clazz = (Class<?>) typeVariable.getBounds()[0];
      } else if (type instanceof GenericArrayType) {
         return Object[].class;
      } else {
         throw new ConverterException("Unknown type: " + type);
      }

      return clazz;
   }

   public static Class<?> getWrapClass(Class<?> clazz) {
      Class<?> wrapClass = m_primitiveTypeMap.get(clazz);

      if (wrapClass != null) {
         return wrapClass;
      } else {
         return clazz;
      }
   }

   public static boolean isTypeSupported(Type fromType, Type... types) {
      Class<?> rawFromType = getRawType(fromType);

      for (Type type : types) {
         Class<?> rawType = getRawType(type);

         if (rawType == rawFromType || rawType.isAssignableFrom(rawFromType)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isUserDefinedClass(Class<?> clazz) {
      return !clazz.isPrimitive() && !clazz.getPackage().getName().startsWith("java");
   }
}
