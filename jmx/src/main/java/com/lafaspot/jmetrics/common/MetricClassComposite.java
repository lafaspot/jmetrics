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
import java.util.List;

import javax.annotation.Nonnull;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

/**
 * CompositeType for monitor class.
 *
 * @author KevinL
 *
 */
public class MetricClassComposite {
    /** All the methods that has metric annotation. */
    @Nonnull
    private final String[] methodNames;
    /** Class CompositeType. */
    private final CompositeType metricClassCompositeType;
    /** Method description. */
    private static final String METHOD_DESCRIPTION = "Method: ";

    /**
     * Constructor.
     *
     * @param className used to Construct type
     * @param metricClassName MetricClass name store in CompositeData type Description
     * @param methods contains metric annotation
     * @param methodCompositeType method CompositeType
     * @throws OpenDataException Create class compositeType fail
     */
    public MetricClassComposite(@Nonnull final String className, @Nonnull final String metricClassName, @Nonnull final List<Method> methods,
            @Nonnull final MetricMethodComposite methodCompositeType) throws OpenDataException {
        final int size = methods.size();

        methodNames = new String[size];
        final String[] methodDescriptions = new String[size];
        @SuppressWarnings("rawtypes")
        final OpenType[] methodTypes = new OpenType[size];
        for (int i = 0; i < size; i++) {
            final Method method = methods.get(i);
            methodNames[i] = method.getName();
            methodDescriptions[i] = METHOD_DESCRIPTION + methodNames[i];
            methodTypes[i] = methodCompositeType.getCompositeType();
        }
        metricClassCompositeType = new CompositeType(className, metricClassName, methodNames, methodDescriptions, methodTypes);
    }

    /**
     * Get the method names as key.
     * @return method array of one monitor
     */
    protected String[] getMethodNames() {
        return methodNames;
    }

    /**
     * Get class compositeType.
     * @return the class CompositeType
     */
    protected CompositeType getCompositeType() {
        return metricClassCompositeType;
    }
}