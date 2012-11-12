package org.unidal.eunit.testfwk.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * JDK defines Class, Method and Field to represent class, method and field
 * instance for reflection. But there is no class defined for method
 * argument/parameter.
 * <p>
 * 
 * The purpose of this class is to simulate a wrapper for method
 * argument/parameter, which is an AnnotatedElement.
 */
public class Parameter extends AccessibleObject {
   private Method m_method;

   private int m_argumentIndex; // 0-based index

   private Class<?> m_argumentType;

   private Annotation[] m_annotations;

   public Parameter(Method method, int argumentIndex, Class<?> argumentType, Annotation[] annotations) {
      m_method = method;
      m_argumentIndex = argumentIndex;
      m_argumentType = argumentType;
      m_annotations = annotations;
   }

   public int getArgumentIndex() {
      return m_argumentIndex;
   }

   @Override
   public Annotation[] getAnnotations() {
      return m_annotations;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      for (Annotation annotation : m_annotations) {
         if (annotation.annotationType() == annotationClass) {
            return (T) annotation;
         }
      }

      return null;
   }

   public Class<?> getArgumentType() {
      return m_argumentType;
   }

   public Method getMethod() {
      return m_method;
   }
}