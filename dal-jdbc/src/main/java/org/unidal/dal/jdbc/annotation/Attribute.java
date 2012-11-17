package org.unidal.dal.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attribute {
   String field();

   boolean primaryKey() default false;

   boolean nullable() default true;

   boolean autoIncrement() default false;

   String selectExpr() default "";

   String insertExpr() default "";

   String updateExpr() default "";
}
