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

import com.lafaspot.jmetrics.annotation.Metric;
/**
 * BaseMonitor to be used by all JMX monitors.
 * 
 * @author manish211
 * 
 */
public interface BaseMonitor {

    /**
     * @param directory {@link MonitorDirectory} instance
     * @param beanName MbeanName to register.
     */
    void setBeanName(MonitorDirectory<?> directory, String beanName);

    /**
     * @return Return the window time in milliseconds. Monitor will be reset/flipped after this window period.
     */
    long getWindow();

    /**
     * @return Returns the uptime of this monitor
     */
    long getUptime();

    /**
     * @return Returns the time when last flip was made.
     */
    long getLastUpdate();

    /**
     * @return Returns the time when last write was made.
     */
    long getLastWrite();

    /**
     * @return Returns the time - which says after what time the monitor will be deregistered incase there is no activity.
     */
    long getExpireTime();

    /**
     * Unregister the Mbean from MBean server. This is required to avoid cluttering of monitors which have no activity.
     */
    void unRegisterMBean();
    
    /**
     * get the metric annotation info.
     */
    Metric getMetricAnnotation(final String methodName);

    /**
     * get the metric class annotation info.
     */
    String getMetricClassName();
}
