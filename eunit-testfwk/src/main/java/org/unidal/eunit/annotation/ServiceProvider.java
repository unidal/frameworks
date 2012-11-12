package org.unidal.eunit.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import org.unidal.eunit.testfwk.spi.IAnnotationHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ServiceProvider {
   Class<? extends IAnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> value();
}
