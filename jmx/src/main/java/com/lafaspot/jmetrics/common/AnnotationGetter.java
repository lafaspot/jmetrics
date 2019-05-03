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

package com.lafaspot.jmetrics.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lafaspot.jmetrics.annotation.Metric;
import com.lafaspot.jmetrics.annotation.MetricClass;

/**
 * Metric annotation getter.
 *
 * @author kevinliu
 */
public class AnnotationGetter {
    /**
     * get the metric annotation info.
     *
     * @param methodName the method name Calling.
     * @return Metric
     */
    public Metric getMetricAnnotation(@Nonnull final String methodName) {
        try {
            final Metric metric = getClass().getDeclaredMethod("get" + methodName).getAnnotation(Metric.class);
            return metric;
        } catch (final NoSuchMethodException e) {
            return null;
        }
}

    /**
     * get the metric class annotation info.
     *
     * @return metricClassName
     */
    @Nullable
    public String getMetricClassName() {
        final MetricClass metricClass = getClass().getAnnotation(MetricClass.class);
        if (!metricClass.enable()) {
            return null;
        }
        return metricClass.name();
    }
}
