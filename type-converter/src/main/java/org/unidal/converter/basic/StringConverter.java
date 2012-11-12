package org.unidal.converter.basic;

import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;

public class StringConverter implements Converter<String> {
   public boolean canConvert(Type fromType, Type targetType) {
      return true;
   }

   public String convert(Object from, Type targetType) throws ConverterException {
      return from.toString();
   }

   public Type getTargetType() {
      return String.class;
   }
}
