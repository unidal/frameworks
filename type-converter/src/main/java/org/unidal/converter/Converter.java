package org.unidal.converter;

import java.lang.reflect.Type;

public interface Converter<T> {
   public boolean canConvert(Type fromType, Type targetType);

   public Type getTargetType();

   public T convert(Object from, Type targetType) throws ConverterException;
}
