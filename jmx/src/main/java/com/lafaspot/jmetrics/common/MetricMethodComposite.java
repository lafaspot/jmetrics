package com.lafaspot.jmetrics.common;

import javax.annotation.Nonnull;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * CompositeType for metric method. Same for all method.
 *
 * @author KevinL
 *
 */
public class MetricMethodComposite {
    /** Method metric name and type. */
    @Nonnull
    private final String[] methodCompositeMetric;
    /** method CompositeType. */
    @Nonnull
    private final CompositeType methodCompositeType;

    /**
     * Constructor.
     *
     * @throws OpenDataException Create method compositeType fail
     */
    protected MetricMethodComposite() throws OpenDataException {
        methodCompositeMetric = new String[2];
        methodCompositeMetric[0] = "name";
        methodCompositeMetric[1] = "type";

        final String[] methodCompositeDescriptions = new String[2];
        methodCompositeDescriptions[0] = "metric name";
        methodCompositeDescriptions[1] = "metric type";

        @SuppressWarnings("rawtypes")
        final OpenType[] methodMetricTypes = new OpenType[2];
        methodMetricTypes[0] = SimpleType.STRING;
        methodMetricTypes[1] = SimpleType.STRING;
            methodCompositeType = new CompositeType("methodName", "Method Name for each metric annotation", methodCompositeMetric,
                    methodCompositeDescriptions, methodMetricTypes);
    }

    /**
     * Get the method metric keys.
     * @return metric name and metric type
     */
    @Nonnull
    protected String[] getMethodCompositeMetric() {
        return methodCompositeMetric;
    }

    /**
     * Get the method CompositeType.
     * @return the method CompositeType
     */
    @Nonnull
    protected CompositeType getCompositeType() {
        return methodCompositeType;
    }
}
