package org.unidal.eunit.handler.junit;

import java.lang.reflect.AnnotatedElement;

import org.junit.Ignore;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;

public enum IgnoreHandler implements IAnnotationHandler<Ignore, AnnotatedElement> {
   INSTANCE;

   @Override
   public Class<Ignore> getTargetAnnotation() {
      return Ignore.class;
   }

   @Override
   public void handle(IClassContext context, Ignore meta, AnnotatedElement annotated) {
      IEunitContext ctx = context.forEunit();
      Object parent = ctx.peek();

      if (parent instanceof EunitClass) {
         EunitClass eunitClass = (EunitClass) parent;

         eunitClass.setIgnored(true);
      } else if (parent instanceof EunitMethod) {
         EunitMethod eunitMethod = (EunitMethod) parent;

         eunitMethod.setIgnored(true);
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
