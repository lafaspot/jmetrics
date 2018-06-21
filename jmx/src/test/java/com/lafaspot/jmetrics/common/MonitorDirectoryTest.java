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

import java.util.concurrent.TimeUnit;

import com.lafaspot.common.types.TimeValue;
import org.testng.annotations.Test;


import com.lafaspot.jmetrics.jmx_sample.ContainerMonitor;

/**
 * Test for MonitorDirectory.
 *
 */
public class MonitorDirectoryTest {
    /**
     * Method to get monitor.
     */
    @Test(threadPoolSize = 8, invocationCount = 500)
    public void getMonitor() {
        final MonitorDirectory<ContainerMonitor> directory = new MonitorDirectory<ContainerMonitor>(ContainerMonitor.class,
                new TimeValue(3, TimeUnit.SECONDS), new TimeValue(3, TimeUnit.SECONDS));

        directory.getMonitor("one");
        directory.getMonitor("one");
        directory.getMonitor("one");
        directory.getMonitor("one");
        directory.getMonitor("two");
        directory.getMonitor("two");
        directory.removeMBean("one");
        directory.removeMBean("one");
    }
}
