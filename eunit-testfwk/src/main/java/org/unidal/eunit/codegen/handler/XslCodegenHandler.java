package org.unidal.eunit.codegen.handler;

import java.lang.reflect.Method;

import org.unidal.eunit.codegen.XslCodegen;
import org.unidal.eunit.codegen.xsl.XslCodegenValve;
import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.task.Priority;

enum XslCodegenHandler implements IAnnotationHandler<XslCodegen, Method> {
   INSTANCE;

   @Override
   public Class<XslCodegen> getTargetAnnotation() {
      return XslCodegen.class;
   }

   @Override
   public void handle(IClassContext ctx, XslCodegen meta, Method method) {
      ctx.getRegistry().registerCaseValve(Priority.MIDDLE, new XslCodegenValve(meta));
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
