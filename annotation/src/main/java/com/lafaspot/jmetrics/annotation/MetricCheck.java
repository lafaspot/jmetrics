/*
 * Copyright [yyyy] [name of copyright owner]
 * 
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  ====================================================================
 */

package com.lafaspot.jmetrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for MetricCheck.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MetricCheck {
	/**
	 * @return Enable/Disable the metric check annotation.
	 */
	boolean enable() default true;

	/**
	 * @return Metric type.
	 */
	String type() default "";

	/**
	 * @return Expression which allows different method as part of the calculation.
	 */
	String expression() default "";

	/**
	 * @return Metric maximum threshold value.
	 */
	long max() default 0;

	/**
	 * @return Metric minimum threshold value.
	 */
	long min() default 0;

	/**
	 * @return Metric maximum deviation threshold allowed for given type.
	 */
	double maxDeviation() default 0;
}
