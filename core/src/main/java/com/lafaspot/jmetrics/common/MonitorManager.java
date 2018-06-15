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

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

/**
 * Creates and hands out BaseMonitor objects to callers. Also has utility methods for any tasks that deal with the entire BaseMonitor collection.
 * @param <T> the type parameter
 * @author lafa
 */
public class MonitorManager<T extends BaseMonitor> {
    /** The id used to distinguish monitors of different classloaders. */
    public static final String ID = Integer.toString(new Random(System.nanoTime()).nextInt());
    private final MonitorDirectory<T> monitorDirectory;
    private final Class<T> clazz;
    private final Set<String> constNamespaceSet;
    /**
     * Constructor.
     * @param clazz class
     * @param monitorDirectory MonitorDirectory Object
     * @param constNamespaceSet Set of namespace
     */
    public MonitorManager(final Class<T> clazz, final MonitorDirectory<T> monitorDirectory, final Set<String> constNamespaceSet) {
        this.clazz = clazz;
        this.monitorDirectory = monitorDirectory;
        this.constNamespaceSet = constNamespaceSet;
    }

    /**
     * Manages handing out and creating Monitor objects. If monitor object does not exist it creates a new one. By default it adds
     * hostname|coloname|clustername|clusterType to namespace list while creating monitor.
     *
     * @param namespace List of names to be used in case monitor object needs to be created.
     * @return Monitor instance of type T corresponding to namespace defined in passed param.
     */
    public T getMonitor(@Nonnull final List<String> namespace) {
    	final Set<String> orderedNamespace = new TreeSet<>();
        orderedNamespace.addAll(namespace);
        orderedNamespace.addAll(constNamespaceSet);

        return monitorDirectory.getMonitor(clazz.getClass().getName() + ":namespace="
                + String.join("|", orderedNamespace) + ",type="
                + clazz.getCanonicalName() + ",id=" + ID);
    }
}