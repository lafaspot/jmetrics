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
