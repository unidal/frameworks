package org.unidal.eunit.handler;

import java.lang.reflect.Method;

import org.junit.Test;

import org.unidal.eunit.annotation.ExpectedException;
import org.unidal.eunit.annotation.ExpectedExceptions;
import org.unidal.eunit.model.entity.EunitException;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;

public enum ExpectedExceptionHandler implements IAnnotationHandler<ExpectedException, Method> {
   INSTANCE;

   @Override
   public Class<ExpectedException> getTargetAnnotation() {
      return ExpectedException.class;
   }

   @Override
   public void handle(IClassContext context, ExpectedException meta, Method method) {
      IEunitContext ctx = context.forEunit();
      EunitMethod eunitMethod = ctx.peek();

      if (eunitMethod.getExpectedExceptions().isEmpty()) {
         if (meta.message().length() > 0 && meta.pattern().length() > 0) {
            throw new RuntimeException(String.format(
                  "Annotation(%s) of Method(%s) of class(%s) can't have both message() and pattern() specified!",
                  ExpectedException.class.getName(), method.getName(), context.getTestClass().getName()));
         }

         EunitException exception = new EunitException();

         exception.setType(meta.type());
         exception.setMessage(meta.message());
         exception.setPattern(meta.pattern());
         eunitMethod.getExpectedExceptions().add(exception);
      } else {
         throw new RuntimeException(String.format("Method(%s) of class(%s) can't be annotated with both @%s, @%s and @%s!",
               method.getName(), context.getTestClass().getName(), Test.class.getName(), ExpectedException.class.getName(),
               ExpectedExceptions.class.getName()));
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
