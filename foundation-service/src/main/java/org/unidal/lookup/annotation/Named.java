package org.unidal.lookup.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface Named {
   Class<?> type() default Default.class;

   String value() default "";

   String instantiationStrategy() default "";

   public String PER_LOOKUP = "per-lookup";

   public String ENUM = "enum";

   public static final class Default {
   }
}
