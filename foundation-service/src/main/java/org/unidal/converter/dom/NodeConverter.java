package org.unidal.converter.dom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.unidal.converter.Converter;
import org.unidal.converter.ConverterException;
import org.unidal.converter.ConverterManager;
import org.unidal.converter.ConverterUtil;
import org.unidal.converter.TypeUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeConverter implements Converter<Object> {

   public boolean canConvert(Type fromType, Type targetType) {
      Class<?> fromClass = TypeUtil.getRawType(fromType);

      return Node.class.isAssignableFrom(fromClass);
   }

   public Object convert(Object from, Type targetType) throws ConverterException {
      Node node = (Node) from;
      Class<?> clazz = TypeUtil.getRawType(targetType);
      Class<?> concreteClass = ConverterManager.getInstance().getRegistry().findType(clazz);

      try {
         Object instance = concreteClass.newInstance();

         convertAttributes(instance, node.getAttributes());
         convertNodeList(instance, node.getChildNodes());

         return instance;
      } catch (Exception e) {
         throw new ConverterException("Error when converting from " + from.getClass() + " to " + concreteClass, e);
      }
   }

   private void convertAttributes(Object instance, NamedNodeMap attributes) throws IllegalArgumentException,
         IllegalAccessException, InvocationTargetException {
      Class<?> clazz = instance.getClass();
      int length = attributes.getLength();

      for (int i = 0; i < length; i++) {
         Node attribute = attributes.item(i);
         String methodName = ConverterUtil.getSetMethodName(attribute.getNodeName());
         Method method = ConverterUtil.getSetMethod(clazz, methodName);
         Type parameterType = method.getGenericParameterTypes()[0];
         String text = attribute.getNodeValue();
         Object value = ConverterManager.getInstance().convert(text, parameterType);

         method.invoke(instance, new Object[] { value });
      }
   }

   private void convertNode(Object instance, Node child) throws IllegalArgumentException, IllegalAccessException,
         InvocationTargetException {
      Class<?> clazz = instance.getClass();
      String methodName = ConverterUtil.getSetMethodName(child.getNodeName());
      Method method = ConverterUtil.getSetMethod(clazz, methodName);
      Type parameterType = method.getGenericParameterTypes()[0];
      Class<?> rawType = TypeUtil.getRawType(parameterType);
      Object value;

      if (rawType.isAssignableFrom(child.getClass())) {
         value = child;
      } else if (rawType.isArray() || List.class.isAssignableFrom(rawType)) {
         value = ConverterManager.getInstance().convert(child, parameterType);
      } else {
         try {
            // try to convert to text as posible
            Object text = ConverterManager.getInstance().convert(child, String.class);

            value = ConverterManager.getInstance().convert(text, parameterType);
         } catch (ConverterException e) {
            value = ConverterManager.getInstance().convert(child, parameterType);
         }
      }

      method.invoke(instance, new Object[] { value });
   }

   private void convertNodeList(Object instance, NodeList children) throws IllegalArgumentException,
         IllegalAccessException, InvocationTargetException {
      int length = children.getLength();

      for (int i = 0; i < length; i++) {
         Node child = children.item(i);

         if (child.getNodeType() == Node.COMMENT_NODE) {
            continue;
         }

         convertNode(instance, child);
      }
   }

   public Type getTargetType() {
      return Type.class;
   }
}
