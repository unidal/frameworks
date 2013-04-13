package org.unidal.web.mvc.view.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ElementMeta {
   String value() default ""; // auto detect

   String format() default "";

   boolean multiple() default false;

   String names() default "";
}
