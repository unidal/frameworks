package org.unidal.eunit.handler.junit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.BeforeClass;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;

public enum BeforeClassHandler implements IAnnotationHandler<BeforeClass, Method> {
   INSTANCE;
   
   @Override
   public Class<BeforeClass> getTargetAnnotation() {
      return BeforeClass.class;
   }

   @Override
   public void handle(IClassContext context, BeforeClass meta, Method method) {
      int modifier = method.getModifiers();

      if (Modifier.isPublic(modifier) && Modifier.isStatic(modifier)) {
         IEunitContext ctx = context.forEunit();
         EunitMethod eunitMethod = ctx.peek();

         eunitMethod.setBeforeAfter(Boolean.TRUE);
         eunitMethod.setStatic(true);
      } else {
         throw new RuntimeException(String.format("Method %s() should be public static.", method.getName()));
      }
   }

   @Override
   public boolean isAfter() {
      return false;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}
