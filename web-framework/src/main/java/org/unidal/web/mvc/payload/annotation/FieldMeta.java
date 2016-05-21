package org.unidal.web.mvc.payload.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FieldMeta {
   public static final String NOT_SPECIFIED = "NOT_SPECIFIED";

   String defaultValue() default NOT_SPECIFIED;

   String format() default "";

   boolean file() default false; // file of multipart/form-data

   boolean raw() default false; // raw request content

   String value();
}
