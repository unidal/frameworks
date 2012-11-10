package org.unidal.web.mvc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.unidal.web.mvc.Action;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.Page;

@Retention(RUNTIME)
@Target(METHOD)
public @interface PayloadMeta {
   Class<? extends ActionPayload<? extends Page, ? extends Action>> value();
}
