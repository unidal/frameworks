package org.unidal.converter.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.ConverterManager;
import org.unidal.converter.TypeUtil;

public class ListConverter<T> implements Converter<List<T>> {

   public boolean canConvert(Type fromType, Type targetType) {
      Class<?> fromClass = TypeUtil.getRawType(fromType);

      if (fromClass.isArray()) {
         return true;
      } else if (List.class.isAssignableFrom(fromClass)) {
         return true;
      }

      return false;
   }

   @SuppressWarnings("unchecked")
   public List<T> convert(Object from, Type targetType) throws ConverterException {
      Class<?> clazz = TypeUtil.getRawType(from.getClass());
      Type componentType = TypeUtil.getComponentType(targetType);
      List<T> list;

      if (clazz.isArray()) {
         int length = Array.getLength(from);

         list = new ArrayList<T>(length);

         for (int i = 0; i < length; i++) {
            Object item = Array.get(from, i);
            Object element = ConverterManager.getInstance().convert(item, componentType);

            list.add((T) element);
         }
      } else if (List.class.isAssignableFrom(clazz)) {
         List<T> fromList = (List<T>) from;

         list = new ArrayList<T>(fromList.size());

         for (T item : fromList) {
            Object element = ConverterManager.getInstance().convert(item, componentType);

            list.add((T) element);
         }
      } else {
         throw new ConverterException("Unknown type: " + from.getClass());
      }

      return list;
   }

   public Type getTargetType() {
      return List.class;
   }
}
