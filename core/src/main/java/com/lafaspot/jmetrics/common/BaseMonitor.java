package com.lafaspot.jmetrics.common;

/**
 * BaseMonitor to be used by all JMX monitors.
 * 
 * @author jaikit
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
}
