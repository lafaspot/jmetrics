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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Creates and hands out Monitor objects to callers. Also has utility methods for any tasks that deal with the entire HttpClientMonitor collection.
 *
 * @author jaikit
 * @param <T> the type parameter
 */
public class MonitorDirectory<T extends BaseMonitor> {

    /** Map of monitor beans. */
    private volatile ConcurrentHashMap<String, T> directory = new ConcurrentHashMap<String, T>();
    private final Object lock = new Object();
    private final Class<T> clazz;
    private final TimeValue window;
    private final TimeValue expire;

    /**
     * @param clazz Monitor class
     * @param window Time to flip the monitoring stats.
     * @param expire Time to expire if there is no activity on this monitor.
     */
    public MonitorDirectory(final Class<T> clazz, final TimeValue window, final TimeValue expire) {
        this.clazz = clazz;
        this.window = window;
        this.expire = expire;
    }

    /**
     * Look up monitor for given key and create new monitor in case look up fails.
     *
     * @param key MBeanname - which needs to be looked up in directory. created
     * @return T MBean for given key.
     */
    public T getMonitor(final String key) {
        if (directory.containsKey(key)) {
            return directory.get(key);
        } else {
            T monitor = null;
            synchronized (lock) {
                /* return MBean if it is already registered by previous thread. */
                if (directory.containsKey(key)) {
                    return directory.get(key);
                }
                try {
                    monitor = this.clazz.getConstructor(TimeValue.class, TimeValue.class).newInstance(this.window, this.expire);
                    monitor.setBeanName(this, key);
                } catch (final InstantiationException | IllegalAccessException
                		| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException("Failed to create monitor instance. ", e);
                }
                directory.putIfAbsent(key, monitor);
            }
            // register bean if not there
            return monitor;
        }
    }

    /**
     * Removes the bean from directory.
     *
     * @param beanName to be removed
     */
    public void removeMBean(final String beanName) {
        directory.remove(beanName);
    }
}