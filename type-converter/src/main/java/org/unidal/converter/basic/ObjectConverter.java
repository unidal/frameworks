package org.unidal.converter.basic;

import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;

public class ObjectConverter implements Converter<Object> {
   public boolean canConvert(Type fromType, Type targetType) {
      return true;
   }

   public Object convert(Object from, Type targetType) throws ConverterException {
      return from;
   }

   public Type getTargetType() {
      return Object.class;
   }
}
