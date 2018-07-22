package org.unidal.eunit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expected exception should be thrown by the test case.
 * <p>
 * 
 * Notes: RuntimeException will be thrown out if both @Test(expected) and @ExpectedException was used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ExpectedException {
   /**
    * Exception type class
    * 
    * @return Throwable or its subclass
    */
   Class<? extends Throwable> type();

   /**
    * Expected exact same message.
    * 
    * @return error message
    */
   String message() default "";

   /**
    * Expected message to match the pattern of MessageFormat.
    * 
    * @return message pattern
    */
   String pattern() default "";
}
