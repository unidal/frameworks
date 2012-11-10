package org.unidal.web.mvc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.unidal.web.mvc.PageHandler;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ModulePagesMeta {
	Class<? extends PageHandler<?>>[] value();
}
