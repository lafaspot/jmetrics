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

    /** Metric name string. */
    private static final String MERIC_NAME = "name";

    /** Metric type string. */
    private static final String MERIC_TYPE = "type";

    /** Metric name string description. */
    private static final String MERIC_NAME_DESCRIPTION = "metric name";

    /** Metric type string description. */
    private static final String MERIC_TYPE_DESCRIPTION = "metric type";

    /** Method compositeType name */
    private static final String METHOD_COMPOSITETYPE_NAME = "methodName";

    /** Method compositeType description. */
    private static final String METHOD_COMPOSITETYPE_DESCRIPTION = "Method Name for each metric annotation";

    /**
     * Constructor.
     *
     * @throws OpenDataException Create method compositeType fail
     */
    protected MetricMethodComposite() throws OpenDataException {
        methodCompositeMetric = new String[2];
        methodCompositeMetric[0] = MERIC_NAME;
        methodCompositeMetric[1] = MERIC_TYPE;

        final String[] methodCompositeDescriptions = new String[2];
        methodCompositeDescriptions[0] = MERIC_NAME_DESCRIPTION;
        methodCompositeDescriptions[1] = MERIC_TYPE_DESCRIPTION;

        @SuppressWarnings("rawtypes")
        final OpenType[] methodMetricTypes = new OpenType[2];
        methodMetricTypes[0] = SimpleType.STRING;
        methodMetricTypes[1] = SimpleType.STRING;
        methodCompositeType = new CompositeType(METHOD_COMPOSITETYPE_NAME, METHOD_COMPOSITETYPE_DESCRIPTION, methodCompositeMetric,
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
