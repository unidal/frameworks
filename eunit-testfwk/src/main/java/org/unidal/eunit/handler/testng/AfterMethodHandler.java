package org.unidal.eunit.handler.testng;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.testng.annotations.AfterMethod;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;

public enum AfterMethodHandler implements IAnnotationHandler<AfterMethod, Method> {
   INSTANCE;

   @Override
   public Class<AfterMethod> getTargetAnnotation() {
      return AfterMethod.class;
   }

   @Override
   public void handle(IClassContext context, AfterMethod meta, Method method) {
      int modifier = method.getModifiers();

      if (Modifier.isPublic(modifier)) {
         IEunitContext ctx = context.forEunit();
         EunitMethod eunitMethod = ctx.peek();

         eunitMethod.setBeforeAfter(Boolean.FALSE);
      } else {
         throw new RuntimeException(String.format("Method %s() should be public.", method.getName()));
      }
   }

   @Override
   public boolean isAfter() {
      return false;
   }

   @Override
   public String toString() {
      return String.format("%s.%s", getClass().getSimpleName(), name());
   }
}
