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

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Monitor CompositeData extractor.
 *
 * @author KevinL
 *
 */
public class MonitorCompositeDataTest {
    /**
     * Create compositeData for a single monitor
     *
     * @throws Exception from creating Composite
     */
    @Test
    public void testMonitorMetricCompositeDataScanner() throws Exception {
        final MonitorScanner monitorDataScanner = new MonitorScanner();
        monitorDataScanner.registerMBean("com.lafaspot.jmetrics.common:type=MonitorMetric");

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        final CompositeData result = (CompositeData) mbs.getAttribute(new ObjectName("com.lafaspot.jmetrics.common:type=MonitorMetric"),
                "AllMonitorCompositeData");
        final CompositeData vipMonitorCompositeData = (CompositeData) result.get("com.lafaspot.jmetrics.common.ContainerMonitor");

        Assert.assertEquals(result.getCompositeType().keySet().size(), 1, "monitor number size not matched");
        Assert.assertEquals(vipMonitorCompositeData.getCompositeType().getTypeName(), "com.lafaspot.jmetrics.common.ContainerMonitor");
        Assert.assertEquals(vipMonitorCompositeData.getCompositeType().getDescription(), "ContainerMonitor");

        Set<String> methods = vipMonitorCompositeData.getCompositeType().keySet();
        Assert.assertEquals(methods.size(), 15, "Method number not matched");
        Assert.assertTrue(methods.contains("getMaxTime"));
        Assert.assertTrue(methods.contains("getLatency"));
        Assert.assertTrue(methods.contains("getRequests"));
        Assert.assertTrue(methods.contains("getErrors"));
        Assert.assertTrue(methods.contains("getBytesReceived"));
        Assert.assertTrue(methods.contains("getBytesSent"));
        Assert.assertTrue(methods.contains("getErrorPercentage"));
        Assert.assertTrue(methods.contains("getRejected"));
        Assert.assertTrue(methods.contains("getResponses1xx"));
        Assert.assertTrue(methods.contains("getResponses2xx"));
        Assert.assertTrue(methods.contains("getResponses3xx"));
        Assert.assertTrue(methods.contains("getResponses4xx"));
        Assert.assertTrue(methods.contains("getResponses5xx"));
        Assert.assertTrue(methods.contains("getActiveRequests"));
        Assert.assertTrue(methods.contains("getMaxActiveRequests"));

        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getMaxTime")).get("name"), "",
                "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getMaxTime")).get("type"), "count",
                "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getLatency")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getLatency")).get("type"), "latency", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getRequests")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getRequests")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getErrors")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getErrors")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getBytesReceived")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getBytesReceived")).get("type"), "count",
                "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getBytesSent")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getBytesSent")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getErrorPercentage")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getErrorPercentage")).get("type"), "latency", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getRejected")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getRejected")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses1xx")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses1xx")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses2xx")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses2xx")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses3xx")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses3xx")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses4xx")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses4xx")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses5xx")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getResponses5xx")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getActiveRequests")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getActiveRequests")).get("type"), "count", "metric type not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getMaxActiveRequests")).get("name"), "", "metric name not match");
        Assert.assertEquals(((CompositeData) vipMonitorCompositeData.get("getMaxActiveRequests")).get("type"), "count", "metric type not match");

    }


}
