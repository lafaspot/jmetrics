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

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * ContainerMonitorManager.
     */
    private MonitorManager<ContainerMonitor> containerMonitorManager;
    /**
     * Initialize parameters for MonitorManager.
     * @throws URISyntaxException when creating URI object
     */
    @SuppressWarnings("deprecation")
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
     * Custom class loader.
     *
     */
    static class CustomClassLoader extends ClassLoader {

        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            if (!name.equals(ContainerMonitor.class.getCanonicalName())) {
                return super.loadClass(name);
            }
            try {
                InputStream inputStream = getSystemResourceAsStream("com/lafaspot/jmetrics/common/ContainerMonitor.class");
                byte[] byteArray = new byte[100000];
                int length  = inputStream.read(byteArray);
                inputStream.close();
                return defineClass(name, byteArray, 0, length);
            } catch (IOException e) {
                throw new ClassNotFoundException();
            }
        }
    }

    /**
     * Test to ensure there is only one instance of ID for all monitor manager instances.
     * @throws ClassNotFoundException when class to be loaded is not found
     */
    @SuppressWarnings({ "unchecked" })
    @Test
    public void checkDifferentIDInstanceForDifferentClassLoader() throws ClassNotFoundException {
        Class<ContainerMonitor> c1 =  (Class<ContainerMonitor>) new CustomClassLoader().loadClass(ContainerMonitor.class.getCanonicalName());
        Class<ContainerMonitor> c2 =  (Class<ContainerMonitor>) new CustomClassLoader().loadClass(ContainerMonitor.class.getCanonicalName());

        final TimeValue window = new TimeValue(new Long("300").longValue(), TimeUnit.MILLISECONDS);
        final TimeValue expire = new TimeValue(new Long("60").longValue(), TimeUnit.SECONDS);
        final Set<String> constNamespaceSet = new TreeSet<String>();
        constNamespaceSet.add("Field1");
        constNamespaceSet.add("Field2");
        constNamespaceSet.add("Field3");
        constNamespaceSet.add("Field4");

        String id1 = new MonitorManager<ContainerMonitor>(c1, new MonitorDirectory<>(c1, window, expire), constNamespaceSet).getId();
        String id2 = new MonitorManager<ContainerMonitor>(c2, new MonitorDirectory<>(c2, window, expire), constNamespaceSet).getId();
        Assert.assertNotEquals(id1, id2, "ID should not be the same since the ContainerMonitor is loaded by different class loaders");
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