package org.unidal.eunit.testfwk.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface IAnnotationHandler<T extends Annotation, S extends AnnotatedElement> {
   /**
    * What's the annotation to be handled.
    * 
    * @return Annotation type
    */
   public Class<T> getTargetAnnotation();

   /**
    * To handle annotation and construct EUnit model or customized model.
    * 
    * @param ctx
    *           Class context
    * @param annotation
    *           annotation instance to be handled
    * @param target
    *           where the annotation be used
    */
   public void handle(IClassContext ctx, T annotation, S target);

   /**
    * Whether this handler should be triggered after the body or before the
    * body.
    * <p>
    * 
    * - For class level annotation, true means that it will be triggered after
    * all methods be processed.<br>
    * - For method level annotation, it will be triggered after all parameters
    * be processed.<br>
    * - For field level annotation, it will be ignored. <br>
    * - For parameter level annotation, it will be ignored. <br>
    * 
    * @return true or false to indicate whether this handler should be triggered
    *         after or before the body.
    */
   public boolean isAfter();
}
