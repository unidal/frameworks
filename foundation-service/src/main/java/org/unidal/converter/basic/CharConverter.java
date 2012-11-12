package org.unidal.converter.basic;

import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.TypeUtil;

public class CharConverter implements Converter<Character> {

   public boolean canConvert(Type fromType, Type targetType) {
      return TypeUtil.isTypeSupported(fromType, Number.class, Boolean.TYPE, Boolean.class, String.class, Enum.class);
   }

   public Character convert(Object from, Type targetType) throws ConverterException {
      if (from instanceof Number) {
         return (char) (((Number) from).intValue() & 0xFFFF);
      } else if (from instanceof Boolean) {
         return ((Boolean) from).booleanValue() ? '1' : '0';
      } else if (from instanceof Enum) {
         return (char) ((Enum<?>) from).ordinal();
      } else {
         String str = from.toString();

         if (str.length() > 0) {
            return str.charAt(0);
         } else {
            return '\0';
         }
      }
   }

   public Type getTargetType() {
      return Character.class;
   }
}
