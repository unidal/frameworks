package org.unidal.web.jsp.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface TaglibMeta {
	String uri();

	String name();

	String shortName();

	String description();

	Class<?>[] funcitons() default {};

	Class<?>[] tags() default {};

	String[] tagFiles() default {};
}
