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

public enum ExpectedExceptionsHandler implements IAnnotationHandler<ExpectedExceptions, Method> {
   INSTANCE;

   @Override
   public Class<ExpectedExceptions> getTargetAnnotation() {
      return ExpectedExceptions.class;
   }

   @Override
   public void handle(IClassContext context, ExpectedExceptions meta, Method method) {
      IEunitContext ctx = context.forEunit();
      EunitMethod eunitMethod = ctx.peek();

      if (eunitMethod.getExpectedExceptions().isEmpty()) {
         for (ExpectedException e : meta.value()) {
            EunitException exception = new EunitException();

            exception.setType(e.type());
            exception.setMessage(e.message());
            exception.setPattern(e.pattern());
            eunitMethod.getExpectedExceptions().add(exception);
         }
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
