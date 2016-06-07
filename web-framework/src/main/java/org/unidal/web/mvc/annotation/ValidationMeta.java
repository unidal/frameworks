package org.unidal.web.mvc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.unidal.web.mvc.Validator;

@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface ValidationMeta {
   Class<? extends Validator<?>>[] value();
}
