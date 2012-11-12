package org.unidal.eunit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Intercept {
   String beforeMethod() default "";

   String afterMethod() default "";

   String onErrorMethod() default "";
}
