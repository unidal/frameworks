package org.unidal.eunit.codegen.handler;

import org.unidal.eunit.codegen.EnableXslCodegen;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.Registry;

public enum EnableXslCodegenHandler implements IAnnotationHandler<EnableXslCodegen, Class<?>> {
   INSTANCE;

   @Override
   public Class<EnableXslCodegen> getTargetAnnotation() {
      return EnableXslCodegen.class;
   }

   @Override
   public void handle(IClassContext ctx, EnableXslCodegen annonation, Class<?> target) {
      Registry registry = ctx.getRegistry();

      registry.registerAnnotationHandler(XslCodegenHandler.INSTANCE);
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
