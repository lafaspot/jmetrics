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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.lafaspot.common.types.TimeValue;

/**
 * This class tests the monitor manager.
 *
 */
public class MonitorManagerTest {
    private MonitorManager<ContainerMonitor> containerMonitorManager;
    /**
     * Initialize parameters for MonitorManager.
     * @throws URISyntaxException when creating URI object
     */
    @BeforeMethod
    public void initialize() throws URISyntaxException {
    	final TimeValue window = new TimeValue(new Long("300").longValue(), TimeUnit.MILLISECONDS);
        final TimeValue expire = new TimeValue(new Long("60").longValue(), TimeUnit.SECONDS);
        final MonitorDirectory<ContainerMonitor> directory = new MonitorDirectory<>(ContainerMonitor.class, window, expire);
        final Set<String> constNamespaceSet = new TreeSet<String>();
        constNamespaceSet.add("Field1");
        constNamespaceSet.add("Field2");
        constNamespaceSet.add("Field3");
        constNamespaceSet.add("Field4");
        containerMonitorManager = new MonitorManager<ContainerMonitor>(ContainerMonitor.class, directory, constNamespaceSet);
        containerMonitorManager.getHostMonitor(new URI("http://oktypes-localhost"));
    }

    /**
     * Test to ensure there is only one instance of ID for all monitor manager instances.
     */
    @Test
    public void checkOneIdInstancePerClassLoader() {
        final TimeValue window = new TimeValue(new Long("400").longValue(), TimeUnit.MILLISECONDS);
        final TimeValue expire = new TimeValue(new Long("50").longValue(), TimeUnit.SECONDS);
        final MonitorDirectory<ContainerMonitor> directory = new MonitorDirectory<>(ContainerMonitor.class, window, expire);
        final Set<String> constNamespaceSet = new TreeSet<String>();
        constNamespaceSet.add("Field11");
        constNamespaceSet.add("Field22");
        constNamespaceSet.add("Field33");
        constNamespaceSet.add("Field44");
        MonitorManager<ContainerMonitor> containerMonitorManager2 = new MonitorManager<ContainerMonitor>(ContainerMonitor.class,
                directory, constNamespaceSet);
        Assert.assertEquals(containerMonitorManager.getId().hashCode(), containerMonitorManager2.getId().hashCode(),
                "There should be only once instance of monitor manager ID per class loader");
    }
    /**
     * Test for valid monitor.
     * 
     * @throws MalformedObjectNameException when error creating ObjectName instance.
     */
    @Test
    public void getValidMonitor() throws MalformedObjectNameException {
        final ContainerMonitor monitor = containerMonitorManager.getMonitor(Arrays.asList("host1"));
        Assert.assertNotNull(monitor);
        final ObjectName beanName = new ObjectName(monitor.getBeanName());
        Assert.assertEquals(beanName.getKeyProperty("type"), "com.lafaspot.jmetrics.common.ContainerMonitor");
        Assert.assertTrue(beanName.getKeyProperty("namespace").contains("host1"));
        Assert.assertEquals(beanName.getDomain(), "java.lang.Class");
        Assert.assertEquals(containerMonitorManager.getMonitor(Arrays.asList("host1")), monitor);
    }
    /**
     * Test for Get Monitor with multiple namespaces.
     * 
     * @throws MalformedObjectNameException when error creating ObjectName instance.
     */
    @Test
    public void getMonitorMultipleNamespaces() throws MalformedObjectNameException {
        final ContainerMonitor monitor = containerMonitorManager.getMonitor(Arrays.asList("host1", "host2"));
        Assert.assertNotNull(monitor);
        Assert.assertEquals(containerMonitorManager.getMonitor(Arrays.asList("host2", "host1")), monitor);
        final ObjectName beanName = new ObjectName(monitor.getBeanName());
        Assert.assertEquals(beanName.getKeyProperty("type"), "com.lafaspot.jmetrics.common.ContainerMonitor");
        Assert.assertTrue(beanName.getKeyProperty("namespace").contains("host1"));
        Assert.assertTrue(beanName.getKeyProperty("namespace").contains("host2"));
        Assert.assertEquals(beanName.getDomain(), "java.lang.Class");
    }
    /**
     * Test for get monitor with spaces in namespace.
     * 
     * @throws MalformedObjectNameException when error creating ObjectName instance.
     */
    @Test
    public void getMonitorWithSpacesInNamespace() throws MalformedObjectNameException {
        final ContainerMonitor monitor = containerMonitorManager.getMonitor(Arrays.asList("host1 01", "host2"));
        Assert.assertNotNull(monitor);
        Assert.assertEquals(containerMonitorManager.getMonitor(Arrays.asList("host2", "host1 01")), monitor);
        final ObjectName beanName = new ObjectName(monitor.getBeanName());
        Assert.assertEquals(beanName.getKeyProperty("type"), "com.lafaspot.jmetrics.common.ContainerMonitor");
        Assert.assertTrue(beanName.getKeyProperty("namespace").contains("host1 01"));
    }
}