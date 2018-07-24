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

/**
 * Container monitor interface.
 *
 */
public interface ContainerMonitorMBean extends BaseMonitor {
    /**
     * @return Maximum latency time in milliseconds by request in last interval.
     */
    long getMaxTime();

    /**
     * @return Total Request count in last interval.
     */
    int getRequests();

    /**
     * @return Total error count in last interval.
     */
    int getErrors();

    /**
     * @return Total bytes received in last interval.
     */
    long getBytesReceived();

    /**
     * @return Total bytes sent in last interval.
     */
    long getBytesSent();

    /**
     * @return Error percentage in last interval.
     */
    float getErrorPercentage();

    /**
     *
     * @return Total number of requests rejected by admin war.
     */
    long getRejected();

    /**
     *
     * @return Number of responses with a 1xx status
     */
    int getResponses1xx();

    /**
     *
     * @return Number of responses with a 2xx status
     */
    int getResponses2xx();

    /**
     *
     * @return Number of responses with a 3xx status
     */
    int getResponses3xx();

    /**
     *
     * @return number of responses with a 4xx status
     */
    int getResponses4xx();

    /**
     *
     * @return number of responses with a 5xx status
     */
    int getResponses5xx();

    /**
     * @return Number of requests currently active
     */
    int getActiveRequests();

    /**
     * @return Maximum number of active requests
     */
    int getMaxActiveRequests();

    /**
     * @return Total latency in milliseconds in last interval.
     */
    long getLatency();

}