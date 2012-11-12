package org.unidal.converter.basic;

import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.TypeUtil;

public class LongConverter implements Converter<Long> {

   public boolean canConvert(Type fromType, Type targetType) {
      return TypeUtil.isTypeSupported(fromType, Number.class, Boolean.TYPE, Boolean.class, String.class, Enum.class);
   }

   public Long convert(Object from, Type targetType) throws ConverterException {
      if (from instanceof Number) {
         return ((Number) from).longValue();
      } else if (from instanceof Boolean) {
         return ((Boolean) from).booleanValue() ? Long.valueOf(1) : 0;
      } else if (from instanceof Enum) {
         return Long.valueOf(((Enum<?>) from).ordinal());
      } else {
         try {
            return Long.valueOf(from.toString());
         } catch (NumberFormatException e) {
            throw new ConverterException(e);
         }
      }
   }

   public Type getTargetType() {
      return Long.class;
   }
}
