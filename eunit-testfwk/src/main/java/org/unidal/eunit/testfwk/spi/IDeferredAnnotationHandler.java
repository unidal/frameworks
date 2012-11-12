package org.unidal.eunit.testfwk.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface IDeferredAnnotationHandler<T extends Annotation, S extends AnnotatedElement> extends IAnnotationHandler<T, S> {
   public IDeferredHandler createDeferredHandler(IClassContext ctx, T meta, S target);

   public static interface IDeferredHandler {
      public void execute();
   }
}
