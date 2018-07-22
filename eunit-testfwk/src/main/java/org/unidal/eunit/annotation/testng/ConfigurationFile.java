package org.unidal.eunit.annotation.testng;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationFile {
   /**
    * XML configuration file in the classpath.
    * 
    * @return XML configuration file in the classpath
    */
   String value();
}
