package org.unidal.eunit.handler;

import org.unidal.eunit.annotation.Id;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;
import org.unidal.eunit.testfwk.spi.Parameter;

public enum IdHandler implements IAnnotationHandler<Id, Parameter> {
   INSTANCE;

   @Override
   public Class<Id> getTargetAnnotation() {
      return Id.class;
   }

   @Override
   public void handle(IClassContext context, Id meta, Parameter parameter) {
      IEunitContext ctx = context.forEunit();
      EunitParameter eunitParameter = ctx.peek();

      eunitParameter.setId(meta.value());
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
