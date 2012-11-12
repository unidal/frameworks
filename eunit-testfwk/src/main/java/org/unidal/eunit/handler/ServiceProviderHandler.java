package org.unidal.eunit.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.unidal.eunit.annotation.ServiceProvider;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IMetaAnnotationHandler;

public enum ServiceProviderHandler implements IMetaAnnotationHandler<ServiceProvider, AnnotatedElement> {
   INSTANCE;

   @Override
   public Class<ServiceProvider> getTargetAnnotation() {
      return ServiceProvider.class;
   }

   @Override
   public void handle(IClassContext ctx, final Annotation annotation, final ServiceProvider meta, AnnotatedElement target,
         boolean after) {
      IAnnotationHandler<Annotation, AnnotatedElement> handler;

      try {
         handler = newInstance(meta.value());
      } catch (Throwable e) {
         throw new IllegalStateException(String.format("Error when creating new instance of %s!", meta.value().getName()), e);
      }

      if (handler.isAfter() == after) {
         handler.handle(ctx, annotation, target);
      }
   }

   @SuppressWarnings("unchecked")
   private IAnnotationHandler<Annotation, AnnotatedElement> newInstance(
         Class<? extends IAnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> clazz) throws Throwable {
      if (clazz.isEnum()) {
         Object[] values;

         try {
            Method method = clazz.getMethod("values");

            if (!method.isAccessible()) {
               method.setAccessible(true);
            }

            values = (Object[]) method.invoke(null);
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }

         if (values.length == 1) {
            Object instance = values[0];

            if (instance instanceof IAnnotationHandler) {
               return (IAnnotationHandler<Annotation, AnnotatedElement>) instance;
            } else {
               throw new RuntimeException(String.format("Enum(%s) should implement %s!", clazz.getName(),
                     IAnnotationHandler.class.getName()));
            }
         } else {
            throw new RuntimeException(String.format("Enum(%s) can only have one enum field!", clazz.getName()));
         }
      } else {
         return (IAnnotationHandler<Annotation, AnnotatedElement>) clazz.newInstance();
      }
   }

   @Override
   public String toString() {
      return String.format("%s.%s", getClass().getSimpleName(), name());
   }
}
