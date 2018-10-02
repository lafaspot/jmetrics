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
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.lafaspot.jmetrics.annotation.Metric;
import com.lafaspot.jmetrics.annotation.MetricCheck;
import com.lafaspot.jmetrics.annotation.MetricClass;
import com.lafaspot.common.types.TimeValue;


/**
 * Records incoming http request stats in an interval.
 *
 */
@MetricClass(enable = true, name = "ContainerMonitor", applications = { "admin" })
public class ContainerMonitor implements ContainerMonitorMBean {
    /**
     * The default namespace to use while creating a monitor for the local instance.
     */
    public static final String DEFAULT_NAMESPACE = "localhost";

    /** value to mutlipe with metrics division. */
    public static final int MULTIPLY = 100;

    /** 0.05 % value. */
    private static final double PCT005 = 0.05;

    /** The state of the monitor. */
    private final MonitorStateHandler<MonitorState> state;
    /** Identifies when the ConatinerMonitor expires. */
    private final TimeValue expire;
    /** Name of the MBean to to be used. */
    private String beanName;
    /** Monitor directory containing a list of monitors. */
    private volatile MonitorDirectory<?> directory;

    /** Min range value for latency for JMetric. */
    private static final long MIN_LATENCY = 0;
    /** Max range value for latency for JMetric. */
    private static final long MAX_LATENCY = 100;
    private static final Set<String> CONST_NAMESPACE_SET = new TreeSet<String>();
    private static final MonitorDirectory<ContainerMonitor> MONITOR_DIRECTORY = new MonitorDirectory<ContainerMonitor>(ContainerMonitor.class,
			new TimeValue(10, TimeUnit.MINUTES), new TimeValue(10, TimeUnit.MINUTES));

    static {
    	CONST_NAMESPACE_SET.add("field1");
    	CONST_NAMESPACE_SET.add("field2");
    	CONST_NAMESPACE_SET.add("field3");
    	CONST_NAMESPACE_SET.add("field4");
    }

    /**
     * Monitor manager holds all references of this type of monitor.
     *
     */
    public enum Manager {
        /** Stores an instance of the Manager. */
        INSTANCE;

        /** Stores an instance of the ContainerMonitor. */
        private final MonitorManager<ContainerMonitor> manager = new MonitorManager<ContainerMonitor>(ContainerMonitor.class,
        		MONITOR_DIRECTORY, CONST_NAMESPACE_SET);

        /**
         * @return monitor manager
         */
        public MonitorManager<ContainerMonitor> getManager() {
            return manager;
        }
    }

    /**
     * Holds state values reported by an executor during a given time window.
     */
    private class MonitorState implements MonitorStateHandler.State<MonitorState> {
        /**
         * Max time of a request.
         */
        private final AtomicLong maxTime = new AtomicLong(0);
        /**
         * Time taken to process.
         */
        private final AtomicLong processingTime = new AtomicLong(0);
        /**
         * Total number of requests.
         */
        private final AtomicInteger requestCount = new AtomicInteger(0);
        /**
         * Total number of errors.
         */
        private final AtomicInteger errorCount = new AtomicInteger(0);
        /**
         * Total number of bytes received in the request.
         */
        private final AtomicLong bytesReceived = new AtomicLong(0);
        /**
         * Total number of bytes sent in the response.
         */
        private final AtomicLong bytesSent = new AtomicLong(0);
        /**
         * Total number of rejected requests.
         */
        private final AtomicLong rejected = new AtomicLong(0);
        /**
         * Responses with status code 100 to 199.
         */
        private final AtomicInteger response1xx = new AtomicInteger(0);
        /**
         * Responses with status code 200 to 299.
         */
        private final AtomicInteger response2xx = new AtomicInteger(0);
        /**
         * Responses with status code 300 to 399.
         */
        private final AtomicInteger response3xx = new AtomicInteger(0);
        /**
         * Responses with status code 400 to 499.
         */
        private final AtomicInteger response4xx = new AtomicInteger(0);
        /**
         * Responses with status code 500 to 599.
         */
        private final AtomicInteger response5xx = new AtomicInteger(0);
        /**
         * Total number or max active requests.
         */
        private final AtomicInteger maxActiveRequests = new AtomicInteger(0);
        /**
         * Number of active requests.
         */
        private final AtomicInteger activeRequests = new AtomicInteger(0);
        /**
         * Stores the time taken for each request.
         */
        private final AtomicLong latency = new AtomicLong(0);
        /**
         * Stores the count of number of latency requests.
         */
        private final AtomicLong latencyCount = new AtomicLong(0);
        /**
         * Time stamp of the last time we wrote to the monitor.
         */
        private final AtomicLong lastWrite = new AtomicLong(System.currentTimeMillis());

        @Override
        public void reset(final MonitorState stable) {
            maxTime.set(0);
            processingTime.set(0);
            requestCount.set(0);
            bytesSent.set(0);
            bytesReceived.set(0);
            errorCount.set(0);
            rejected.set(0);
            response1xx.set(0);
            response2xx.set(0);
            response3xx.set(0);
            response4xx.set(0);
            response5xx.set(0);
            maxActiveRequests.set(0);
            activeRequests.set(0);
            latency.set(0);
            latencyCount.set(0);
            lastWrite.set(stable.lastWrite.get());
        }
    }

    /**
     * Creates the monitor for the container.
     *
     * @param window
     *            The time interval to flip the monitor's state between stable and current
     * @param expire
     *            The time which says after what time the monitor will be unregistered if there is no activity.
     */
    public ContainerMonitor(final TimeValue window, final TimeValue expire) {
        state = new MonitorStateHandler<MonitorState>(new MonitorState(), new MonitorState(), window);
        this.expire = expire;
    }

    @Override
    public void setBeanName(final MonitorDirectory<?> directory, final String beanName) {
        this.beanName = beanName;
        this.directory = directory;
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            try {
                mbs.unregisterMBean(new ObjectName(beanName));
            } catch (final InstanceNotFoundException e) {
            	//swallow
            }
            ObjectName name;
            name = new ObjectName(beanName);
            mbs.registerMBean(this, name);
        } catch (final InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
        	//swallow
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#getWindow()
     */
    @Override
    public long getWindow() {
        state.update();
        return state.getWindow().toMillis();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#getUptime()
     */
    @Override
    public long getUptime() {
        state.update();
        return System.currentTimeMillis() - state.getStartTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#getLastUpdate()
     */
    @Override
    public long getLastUpdate() {
        state.update();
        return state.getLastUpdate();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#getLastWrite()
     */
    @Override
    public long getLastWrite() {
        state.update();
        return state.stable().lastWrite.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#getExpireTime()
     */
    @Override
    public long getExpireTime() {
        return this.expire.toMillis();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.BaseMonitor#unRegisterMBean()
     */
    @Override
    public void unRegisterMBean() {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.unregisterMBean(new ObjectName(beanName));
            directory.removeMBean(beanName);
        } catch (final MBeanRegistrationException | MalformedObjectNameException | InstanceNotFoundException e) {
        	//swallow
        }
    }

    /**
     * Return the bean name.
     *
     * @return the bean name
     */
    public String getBeanName() {
        return this.beanName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getMaxTime()
     */
    @Override
    @Metric(enable = true, type = "count")
    public long getMaxTime() {
        state.update();
        return state.stable().maxTime.get();
    }

    @Override
    @Metric(enable = true, type = "latency", unit = "time")
    @MetricCheck(enable = true, type = "latency", min = MIN_LATENCY, max = MAX_LATENCY)
    public long getLatency() {
        state.update();
        // make sure we only use one instance this method
        final MonitorState stable = state.stable();
        if ((stable.latency.get() <= 0) || (stable.latencyCount.get() <= 0)) {
            return 0;
        }
        return stable.latency.get() / stable.latencyCount.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getRequestCount()
     */
    @Override
    @Metric(enable = true, type = "count")
    public int getRequests() {
        state.update();
        return state.stable().requestCount.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getErrorCount()
     */
    @Metric(enable = true, type = "count")
    @MetricCheck(enable = true, type = "ratio", expression = "Errors / Requests", maxDeviation = PCT005)
    @Override
    public int getErrors() {
        state.update();
        return state.stable().errorCount.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getBytesReceived()
     */
    @Override
    @Metric(enable = true, type = "count")
    public long getBytesReceived() {
        state.update();
        return state.stable().bytesReceived.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getBytesSent()
     */
    @Override
    @Metric(enable = true, type = "count")
    public long getBytesSent() {
        state.update();
        return state.stable().bytesSent.get();
    }

    /*
     * (non-Javadoc) The Metric "type" is kept as 'latency' instead of 'average' since both latency and percentage does the average. MetricCollector
     * does not understand average as of now.
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getErrorPercentage()
     */
    @Override
    @Metric(enable = true, type = "latency", unit = "percentage")
    public float getErrorPercentage() {
        state.update();
        final MonitorState stableState = state.stable();
        if (stableState.errorCount.get() <= 0 || stableState.requestCount.get() <= 0) {
            return 0;
        } else {
            return (float) ((((double) stableState.errorCount.get()) / stableState.requestCount.get()) * MULTIPLY);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getRejected()
     */
    @Override
    @Metric(enable = true, type = "count")
    @MetricCheck(enable = true, type = "ratio", expression = "Rejected / Requests", maxDeviation = PCT005)
    public long getRejected() {
        state.update();
        return state.stable().rejected.get();
    }

    /**
     * Increment number of rejected requests.
     */
    public void incrementRejected() {
        final MonitorState current = state.current();
        current.rejected.incrementAndGet();
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * @param maxTime Maximum time recorded by request in last interval.
     */
    public void setMaxTime(final long maxTime) {
        final MonitorState current = state.current();
        current.maxTime.addAndGet(maxTime);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * Records the processing time in the last interval.
     *
     * @param processingTime Total processing time recorded by tomcat in last interval.
     * @param count number of requests
     *
     */
    public void setProcessingTime(final long processingTime, final int count) {
        final MonitorState current = state.current();
        current.latency.addAndGet(processingTime);
        current.latencyCount.addAndGet(count);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * @param requestcount Total request count in last interval.
     */
    public void setRequestCount(final int requestcount) {
        final MonitorState current = state.current();
        current.requestCount.addAndGet(requestcount);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * @param errorCount Total error count in last interval.
     */
    public void setErrorCount(final int errorCount) {
        final MonitorState current = state.current();
        current.errorCount.addAndGet(errorCount);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * @param bytesReceived Total bytes received in last interval.
     */
    public void setBytesReceived(final long bytesReceived) {
        final MonitorState current = state.current();
        current.bytesReceived.addAndGet(bytesReceived);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     * @param bytesSent Total bytes send in last interval.
     */
    public void setBytesSent(final long bytesSent) {
        final MonitorState current = state.current();
        current.bytesSent.addAndGet(bytesSent);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param response1xx Number of responses with a 1xx status
     */
    public void setResponses1xx(final int response1xx) {
        final MonitorState current = state.current();
        current.response1xx.addAndGet(response1xx);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param response2xx Number of responses with a 2xx status
     */
    public void setResponses2xx(final int response2xx) {
        final MonitorState current = state.current();
        current.response2xx.addAndGet(response2xx);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param response3xx Number of responses with a 3xx status
     */
    public void setResponses3xx(final int response3xx) {
        final MonitorState current = state.current();
        current.response3xx.addAndGet(response3xx);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param response4xx Number of responses with a 4xx status
     */
    public void setResponses4xx(final int response4xx) {
        final MonitorState current = state.current();
        current.response4xx.set(response4xx);
        current.errorCount.addAndGet(response4xx);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param response5xx Number of responses with a 1xx status
     */
    public void setResponses5xx(final int response5xx) {
        final MonitorState current = state.current();
        current.response5xx.set(response5xx);
        current.errorCount.addAndGet(response5xx);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param maxActiveRequests Number of max active requests
     */
    public void setMaxActiveRequests(final int maxActiveRequests) {
        final MonitorState current = state.current();
        current.maxActiveRequests.set(maxActiveRequests);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /**
     *
     * @param activeRequests Number of active requests
     */
    public void setActiveRequests(final int activeRequests) {
        final MonitorState current = state.current();
        current.activeRequests.addAndGet(activeRequests);
        current.lastWrite.set(System.currentTimeMillis());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getResponse1xx()
     */
    @Metric(enable = true, type = "count")
    @Override
    public int getResponses1xx() {
        state.update();
        return state.stable().response1xx.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getResponse2xx()
     */
    @Metric(enable = true, type = "count")
    @Override
    public int getResponses2xx() {
        state.update();
        return state.stable().response2xx.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getResponse3xx()
     */
    @Metric(enable = true, type = "count")
    @Override
    public int getResponses3xx() {
        state.update();
        return state.stable().response3xx.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getResponse4xx()
     */
    @Metric(enable = true, type = "count")
    @MetricCheck(enable = true, type = "ratio", expression = "Responses4xx / Requests", maxDeviation = PCT005)
    @Override
    public int getResponses4xx() {
        state.update();
        return state.stable().response4xx.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getResponse5xx()
     */
    @Metric(enable = true, type = "count")
    @MetricCheck(enable = true, type = "ratio", expression = "Responses5xx / Requests", maxDeviation = PCT005)
    @Override
    public int getResponses5xx() {
        state.update();
        return state.stable().response5xx.get();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getRequestsActive()
     */
    @Metric(enable = true, type = "count")
    @Override
    public int getActiveRequests() {
        state.update();
        return state.stable().activeRequests.get();

    }

    /*
     * (non-Javadoc)
     *
     * @see com.lafaspot.jmetrics.monitor.ContainerMonitorMBean#getRequestsActiveMax()
     */
    @Metric(enable = true, type = "count")
    @Override
    public int getMaxActiveRequests() {
        state.update();
        return state.stable().maxActiveRequests.get();
    }

    /**
     * Flip the monitor's internal state.
     *
     * Deprecated: used by unit tests only
     */
    @Deprecated
    public void flip() {
        state.flip();
    }

}