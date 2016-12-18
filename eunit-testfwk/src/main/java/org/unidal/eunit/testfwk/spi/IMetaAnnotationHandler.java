package org.unidal.eunit.testfwk.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface IMetaAnnotationHandler<T extends Annotation, S extends AnnotatedElement> {
   /**
    * What's the annotation to be handled.
    * 
    * @return Annotation type
    */
   public Class<T> getTargetAnnotation();

   /**
    * To handle annotation and construct EUnit model or customized model.
    * <p>
    * 
    * This method will be called twice with after set to 'false' and 'true'.
    * 
    * @param ctx
    *           Class context
    * @param annotation
    *           annotation instance to be handled
    * @param meta
    *           meta annotation instance to be handled
    * @param target
    *           where the annotation be used
    * @param after
    *           Whether this handler should be triggered after the body or
    *           before the body.
    */
   public void handle(IClassContext ctx, Annotation annotation, T meta, S target, boolean after);
}
