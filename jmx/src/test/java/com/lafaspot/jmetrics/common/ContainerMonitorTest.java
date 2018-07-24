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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lafaspot.common.types.TimeValue;


import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Test class for ContainerMonitor.
 *
 */
public class ContainerMonitorTest {

    /**
     * Test to validate bean name.
     */
    @Test
    public void testSetBeanName() {
        final ContainerMonitor monitor = ContainerMonitor.Manager.INSTANCE.getManager().getMonitor(Arrays.asList("localhost"));
        monitor.setBeanName(null, "name:first:second");
    }

    /**
     * Test to validate monitor.
     *
     * @throws MalformedObjectNameException when error getting monitor
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testMonitor() throws MalformedObjectNameException {
    	final TimeValue window = new TimeValue(new Long("300").longValue(), TimeUnit.MILLISECONDS);
        final TimeValue expire = new TimeValue(new Long("60").longValue(), TimeUnit.SECONDS);
        final MonitorDirectory<ContainerMonitor> directory = new MonitorDirectory<>(ContainerMonitor.class, window, expire);
    	final String beanName = this.getClass().getPackage().getName() + ":type=ContainerMonitorTest,name=testMonitor" + System.currentTimeMillis();
    	// Assert that no registration for this monitor bean exists now.
        Assert.assertFalse(ManagementFactory.getPlatformMBeanServer().isRegistered(new ObjectName(beanName)),
                "The Bean should not be registered after calling unRegisterBean()");
        final ContainerMonitor monitor = ContainerMonitor.Manager.INSTANCE.getManager().getMonitor(Arrays.asList("test1"));
        monitor.setBytesReceived(101);
        monitor.setBytesSent(102);
        monitor.setErrorCount(5);
        monitor.setMaxTime(12);
        monitor.setProcessingTime(20, 10);
        monitor.setProcessingTime(30, 15);
        monitor.setRequestCount(100);
        monitor.incrementRejected();
        monitor.setResponses2xx(2);
        monitor.setResponses1xx(1);
        monitor.setResponses3xx(3);
        monitor.setResponses4xx(4);
        monitor.setResponses5xx(5);
        monitor.setActiveRequests(10);
        monitor.setMaxActiveRequests(1);
        monitor.setBeanName(directory, beanName);
     // Assert if monitor bean registration exists.
        Assert.assertTrue(ManagementFactory.getPlatformMBeanServer().isRegistered(new ObjectName(beanName)), "The Bean should be registered.");

        monitor.flip();

        Assert.assertEquals(monitor.getBytesReceived(), 101);
        Assert.assertEquals(monitor.getBytesSent(), 102);
        Assert.assertEquals(monitor.getErrors(), 14);
        Assert.assertEquals(monitor.getMaxTime(), 12);
        Assert.assertEquals(monitor.getLatency(), 2);
        Assert.assertEquals(monitor.getRequests(), 100);
        Assert.assertEquals(monitor.getErrorPercentage(), (float) 14);
        Assert.assertEquals(monitor.getRejected(), 1);
        Assert.assertEquals(monitor.getResponses1xx(), 1);
        Assert.assertEquals(monitor.getResponses2xx(), 2);
        Assert.assertEquals(monitor.getResponses3xx(), 3);
        Assert.assertEquals(monitor.getResponses4xx(), 4);
        Assert.assertEquals(monitor.getResponses5xx(), 5);
        Assert.assertEquals(monitor.getMaxActiveRequests(), 1);
        Assert.assertEquals(monitor.getActiveRequests(), 10);

        monitor.flip();

        Assert.assertEquals(monitor.getBytesReceived(), 0);
        Assert.assertEquals(monitor.getBytesSent(), 0);
        Assert.assertEquals(monitor.getErrors(), 0);
        Assert.assertEquals(monitor.getMaxTime(), 0);
        Assert.assertEquals(monitor.getLatency(), 0);
        Assert.assertEquals(monitor.getRequests(), 0);
        Assert.assertEquals(monitor.getErrorPercentage(), (float) 0);
        Assert.assertEquals(monitor.getRejected(), 0);
        Assert.assertEquals(monitor.getResponses1xx(), 0);
        Assert.assertEquals(monitor.getResponses2xx(), 0);
        Assert.assertEquals(monitor.getResponses3xx(), 0);
        Assert.assertEquals(monitor.getResponses4xx(), 0);
        Assert.assertEquals(monitor.getResponses5xx(), 0);
        Assert.assertEquals(monitor.getMaxActiveRequests(), 0);
        Assert.assertEquals(monitor.getActiveRequests(), 0);

        Assert.assertNotEquals(monitor.getLastUpdate(), 0);
        Assert.assertNotEquals(monitor.getExpireTime(), 0);
        Assert.assertNotEquals(monitor.getLastWrite(), 0);
        monitor.unRegisterMBean();
    }

}
