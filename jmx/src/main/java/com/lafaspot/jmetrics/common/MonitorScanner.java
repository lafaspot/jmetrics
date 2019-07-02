package com.lafaspot.jmetrics.common;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lafaspot.jmetrics.annotation.MetricClass;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Used by public to get all monitor metricClass name and method metric name, type.
 *
 * @author KevinL
 *
 */
public class MonitorScanner implements MonitorScannerMBean {
    /** Get logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /** Monitor compositeData send to yamas. */
    private CompositeData allMonitorCompositeData;

    /**
     * Construtor. Scan all monitor classes
     */
    public MonitorScanner() {
        allMonitorCompositeData = null;
    }

    /**
     * Get All monitors and store metric metricClass to compositeData[].
     *
     * @throws OpenDataException creating compositetype fail
     */
    private void scan() {
        // Need to fix! Temporary exclude MimeTypeMonitor
        final Set<Class<?>> monitors = new HashSet<>();
        final ClassGraph classGraph = new ClassGraph();
        final String[] blacklistedPackages = { "java", "javafx", "com.sun", "sun.tools", "org.testng", "com.beust.testng", "org.mockito",
                "ch.qos.logback", "org.slf4j", "org.apache", "io.netty", "com.google", "com.yahoo.ymail.xmas.appbase.message" };
        classGraph.enableAnnotationInfo().ignoreClassVisibility().blacklistLibOrExtJars()
                .blacklistPackages(blacklistedPackages).removeTemporaryFilesAfterScan().overrideClassLoaders(this.getClass().getClassLoader())
                .ignoreParentClassLoaders();
        final ScanResult scanResult = classGraph.scan();
        final ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(MetricClass.class.getName());
        monitors.addAll(classInfoList.loadClasses(true));

        final CompositeData[] monitorCompositeData = new CompositeData[monitors.size()];
            int i = 0;
        for (Class<?> monitor : monitors) {
            try {
                monitorCompositeData[i] = new MonitorCompositeData(monitor).getMetricAnnotation();
                i++;
            } catch (OpenDataException ox) {
                logger.error("Getting monitor metricaAnnotation failed: " + monitor.getName());
            }
        }
        final int monitorCount = monitorCompositeData.length;
        final String[] monitorKey = new String[monitorCount];
        final String[] monitorKeyDescription = new String[monitorCount];
        @SuppressWarnings("rawtypes")
        final OpenType[] monitorType = new OpenType[monitorCount];

        for (int j = 0; j < monitorCompositeData.length; j++) {
            final CompositeData monitor = monitorCompositeData[j];
            monitorKey[j] = monitor.getCompositeType().getTypeName();
            monitorKeyDescription[j] = monitor.getCompositeType().getDescription();
            monitorType[j] = monitor.getCompositeType();
        }

        try {
            final CompositeType allMonitorCompositeType = new CompositeType("Monitor Metric", "Monitor Metric Info", monitorKey,
                    monitorKeyDescription, monitorType);
            allMonitorCompositeData = new CompositeDataSupport(allMonitorCompositeType, monitorKey, monitorCompositeData);
        } catch (final OpenDataException ox) {
            logger.error("Creating CompositeData failed", ox);
        }
    }

    /**
     * Create CompositeData for existing monitor.
     *
     * @return monitorCompositeData all monitor CompositeData
     */
    @Override
    public CompositeData getAllMonitorCompositeData() {
        if (allMonitorCompositeData != null) {
            return allMonitorCompositeData;
        }
        scan();
        return allMonitorCompositeData;
    }

    /**
     * Register MBean to mbs.
     *
     * @param beanName name of mbean
     */
    public void registerMBean(final String beanName) {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            try {
                mbs.unregisterMBean(new ObjectName(beanName));
            } catch (final InstanceNotFoundException e) {
                // ignore Instance not found
            }
            mbs.registerMBean(this, new ObjectName(beanName));
        } catch (final InstanceAlreadyExistsException e) {
            logger.error("InstanceAlreadyExistsException", e);
        } catch (final MBeanRegistrationException e) {
            logger.error("MBeanRegistrationException", e);
        } catch (final NotCompliantMBeanException e) {
            logger.error("InstanceAlreadyExistsException", e);
        } catch (final MalformedObjectNameException e) {
            logger.error("InstanceAlreadyExistsException", e);
        }
    }

    /**
     * @param beanName name of mbean. UnregisterMBean.
     */
    public void unRegisterMBean(final String beanName) {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.unregisterMBean(new ObjectName(beanName));
        } catch (final MBeanRegistrationException e) {
            logger.error("failed to de-register bean " + beanName, e);
        } catch (final MalformedObjectNameException e) {
            logger.error("failed to de-register bean " + beanName, e);
        } catch (final InstanceNotFoundException e) {
            logger.error("failed to de-register bean " + beanName, e);
        }
    }

}