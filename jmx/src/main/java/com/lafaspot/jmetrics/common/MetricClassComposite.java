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
            methodDescriptions[i] = "Method " + methodNames[i] + " for name and type";
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