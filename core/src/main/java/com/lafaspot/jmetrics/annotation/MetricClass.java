package com.lafaspot.jmetrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation class which extract metrics out of JMX beans and create
 * definitions files for build jobs pipeline promotions and to setup production
 * alerts based from code repositories.
 * 
 * @author jpowang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MetricClass {
	/**
	 * Name of the annotated metric class.
	 */
	String name();

	/**
	 * Specific application to be applied.
	 */
	String app();

	/**
	 * Enable/Disable the metric class annotation.
	 */
	boolean enable() default true;

}
