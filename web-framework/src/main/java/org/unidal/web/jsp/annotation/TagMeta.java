package org.unidal.web.jsp.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.jsp.tagext.TagExtraInfo;

@Retention(RUNTIME)
@Target(TYPE)
public @interface TagMeta {
	String name();

	String description();

	// available values: 'JSP', 'emtpy', 'tagdependent', 'scriptless'
	String bodyContent() default "JSP";

	boolean dynamicAttributes() default false;

	Class<? extends TagExtraInfo> tagextrainfo() default TagExtraInfo.class;
}