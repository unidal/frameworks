package org.unidal.converter.dom;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.ConverterManager;
import org.unidal.converter.TypeUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeArrayConverter implements Converter<Object> {
   public boolean canConvert(Type fromType, Type targetType) {
      Class<?> fromClass = TypeUtil.getRawType(fromType);

      return Node.class.isAssignableFrom(fromClass);
   }

   public Object convert(Object from, Type targetType) throws ConverterException {
      Node node = (Node) from;

      NodeList children = node.getChildNodes();
      int size = children.getLength();
      List<Object> list = new ArrayList<Object>(size);
      Type componentType = TypeUtil.getComponentType(targetType);
      Class<?> rawComponentType = TypeUtil.getRawType(componentType);

      for (int j = 0; j < size; j++) {
         Node child = children.item(j);

         if (child.getNodeType() == Node.COMMENT_NODE) {
            continue;
         } else if (rawComponentType.isAssignableFrom(child.getClass())) {
            list.add(child);
         } else {
            try {
               // try to convert to text as posible
               Object text = ConverterManager.getInstance().convert(child, String.class);

               list.add(text);
            } catch (ConverterException e) {
               list.add(child);
            }
         }
      }

      Object value = ConverterManager.getInstance().convert(list.toArray(), targetType);

      return value;
   }

   public Type getTargetType() {
      return Array.class;
   }
}
