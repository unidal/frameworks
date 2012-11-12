package org.unidal.junitnexgen.category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Category {
	//Required field
    Groups [] value();
 
    //Optional fields
    Domain domainName() default Domain.Runtime;
    Feature feature() default Feature.Common;  
    String subFeature() default "Undefined"; // DataBinding, configuration, monitoring etc
    String description() default "Junit Test Case";
    String bugID() default "";
    String trainID() default "";
    String projectID() default "";
    String authorDev() default "";
    String authorQE() default "";
    
    //Enum data types
    public enum Domain { Runtime, BuildTime, Tooling, Security, Services }
    public enum Feature {SIF, SPF, Common, Plugin, Codegen, RDS}
	
	public enum Groups {
		P1, 
		P2, 
		P3,
		P4,
		P5,
		GUI,
		HTML,
		JSP,
		SERVLET,
		DB,
		WEB,
		BACKEND,
		UNIT, 
		FUNCTIONAL, 
		INTEGRATION,
		FAST,
		SLOW, 
		LINUX,
		Mac,
		WIN32, 
		REMOTE, 
		LOCAL,
		ONEGBRAM, 
		TWOGBRAM,
		DEBUG,
		IE,
		FF,
		OPERA,
		SAFARI,
		POSITIVE,
		NEGATIVE,
		EDGE,
		NOJARRUN
	}
}
