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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

import com.lafaspot.jmetrics.annotation.Metric;
import com.lafaspot.jmetrics.annotation.MetricClass;

/**
 * Create and return compositedata contains monitor metricClass name and monitor method metric annotation.
 *
 * @author KevinL
 *
 */
public class MonitorCompositeDataBuilder {
    /** Metric CompositeType, same for all methods. */
    private final MetricMethodComposite methodCompositeType;
    /** Monitor composite data. */
    private final CompositeData monitorCompositeData;
    /** Monitor attribute prefix. */
    private static final String GET = "get";

    /**
     * Constructor.
     * @param clazz Monitor class
     * @throws OpenDataException Fail on creating type
     */
    public MonitorCompositeDataBuilder(final Class<?> clazz) throws OpenDataException {
        methodCompositeType = new MetricMethodComposite();
        Method[] methods = clazz.getMethods();
        final List<Method> annotatedMethods = new ArrayList<>();

        for (Method method : methods) {
            if (method.getName().startsWith(GET) && (method.getAnnotation(Metric.class) != null)) {
                annotatedMethods.add(method);
            }
        }
        final MetricClassComposite monitorCompositeType = new MetricClassComposite(clazz.getName(), clazz.getAnnotation(MetricClass.class).name(),
                annotatedMethods,
                methodCompositeType);

        final int size = annotatedMethods.size();
        final CompositeData[] compositeData = new CompositeData[size];
        for (int i = 0; i < size; i++) {
            final Method method = annotatedMethods.get(i);
            final Metric metric = method.getAnnotation(Metric.class);
            compositeData[i] = getCompositeData(metric.name(), metric.type());
        }
        monitorCompositeData = new CompositeDataSupport(monitorCompositeType.getCompositeType(),
                monitorCompositeType.getMethodNames(), compositeData);
    }

    /**
     * Construct CompositeData for one monitor.
     *
     * @return The CompositeData of the monitor.
     * @throws OpenDataException Create CompositeData fail
     */
    protected CompositeData getMetricClassData() throws OpenDataException {
        return monitorCompositeData;
    }

    /**
     * Construct CompositeData for single method.
     *
     * @param name method metric name
     * @param type method metric type
     * @return CompositeData of single method
     * @throws OpenDataException Creating CompositeData fail
     */
    private CompositeData getCompositeData(final String name, final String type) throws OpenDataException {
        final Object[] methodMetricsValue = new Object[2];
        methodMetricsValue[0] = name;
        methodMetricsValue[1] = type;
        return new CompositeDataSupport(methodCompositeType.getCompositeType(), methodCompositeType.getMethodCompositeMetric(),
                methodMetricsValue);

    }
}
