package org.unidal.lookup.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD })
public @interface Inject {
	Class<?> type() default Default.class;

	String value() default "";

	public static final class Default {
	}
}
