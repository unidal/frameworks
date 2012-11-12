package org.unidal.eunit.codegen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.unidal.eunit.annotation.ServiceProvider;
import org.unidal.eunit.codegen.handler.EnableXslCodegenHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceProvider(EnableXslCodegenHandler.class)
public @interface EnableXslCodegen {
   
}
