package org.unidal.converter.dom;

import java.lang.reflect.Type;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.TypeUtil;
import org.w3c.dom.Node;

public class NodeValueConverter implements Converter<String> {
   public boolean canConvert(Type fromType, Type targetType) {
      Class<?> fromClass = TypeUtil.getRawType(fromType);

      return Node.class.isAssignableFrom(fromClass);
   }

   public String convert(Object from, Type targetType) throws ConverterException {
      Node node = (Node) from;

      switch (node.getNodeType()) {
      case Node.ATTRIBUTE_NODE:
         return node.getNodeValue();
      case Node.ELEMENT_NODE:
         Node firstChild = node.getFirstChild();

         switch (firstChild.getNodeType()) {
         case Node.TEXT_NODE:
         case Node.CDATA_SECTION_NODE:
            return firstChild.getNodeValue();
         }
      }

      throw new ConverterException("Can't convert from " + from + " to " + targetType);
   }

   public Type getTargetType() {
      return String.class;
   }
}
