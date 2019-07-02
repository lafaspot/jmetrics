package com.lafaspot.jmetrics.common;

import javax.management.openmbean.CompositeData;

/**
 * Monitor MetricCompositeData MBean to expose MonitorAnnotationCompositeData.
 *
 * @author yliu01
 *
 */
public interface MonitorScannerMBean {
    /**
     * @return the CompossiteData of monitor metric.
     */
    CompositeData getAllMonitorCompositeData();
}
