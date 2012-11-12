package org.unidal.converter.advanced;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.ConverterManager;
import org.unidal.converter.TypeUtil;

public class ConstructorConverter implements Converter<Object> {

   public boolean canConvert(Type fromType, Type targetType) {
      Class<?> fromClass = TypeUtil.getRawType(fromType);
      Class<?> targetClass = TypeUtil.getRawType(targetType);

      return getSingleParameterConstructor(fromClass, targetClass) != null;
   }

   public Object convert(Object from, Type targetType) throws ConverterException {
      Class<?> targetClass = TypeUtil.getRawType(targetType);
      Constructor<?> c = getSingleParameterConstructor(from.getClass(), targetClass);

      try {
         Object value = ConverterManager.getInstance().convert(from, c.getParameterTypes()[0]);

         return c.newInstance(new Object[] { value });
      } catch (Exception e) {
         throw new ConverterException("Can't create instance of " + targetType + " for " + from.getClass());
      }
   }

   private Constructor<?> getSingleParameterConstructor(Class<?> fromClass, Class<?> targetClass) {
      Constructor<?>[] constructors = targetClass.getConstructors();

      if (fromClass.isPrimitive()) {
         fromClass = TypeUtil.getWrapClass(fromClass);
      }

      for (Constructor<?> c : constructors) {
         int m = c.getModifiers();
         Class<?>[] types = c.getParameterTypes();

         if (Modifier.isPublic(m) && types.length == 1) {
            Class<?> parameterType = types[0];

            if (parameterType.isPrimitive()) {
               parameterType = TypeUtil.getWrapClass(parameterType);
            }

            if (parameterType.isAssignableFrom(fromClass)) {
               return c;
            }
         }
      }

      return null;
   }

   public Type getTargetType() {
      return Type.class;
   }
}
