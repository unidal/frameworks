package org.unidal.web.jsp.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AttributeMeta {
	String name() default "";

	String description() default "";

	boolean required() default false;

	boolean rtexprvalue() default true;

	boolean fragment() default false;

}