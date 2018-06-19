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
package com.lafaspot.jmetrics.monitor;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import com.lafaspot.jmetrics.datatype.TimeValue;
import com.lafaspot.jmetrics.monitor.MonitorStateHandler.State;


/**
* Handler to be used to manager monitor state changes for monitor that use 2 instances of counters. One instance (current) is the write copy, this
* copy is becomes the stable copy once the timwWindow expires, the update method should be called frequently to allow for the flip to happen.
*
* @author manish211
*
* @param <T> - client state class
*/
public class MonitorStateHandler<T extends State<T>> {
   /**
    * State classed should implement this interface. update method will call the reset method on the state class, after a flip from current to
    * stable. the reset() will be called on the old stable state instance.
    *
    * @param <T> type of state
    */
   public interface State<T> {
       /**
        * reset the state.
        *
        * @param stableState the stable state
        */
       void reset(@Nonnull T stableState);
   }

   /** time when this state started. */
   private final long startTime = System.currentTimeMillis();

   /** time when this state was last flipped. */
   private AtomicLong lastFlip = new AtomicLong(System.currentTimeMillis());

   /** Time windows when read state is replace with current state. */
   private final TimeValue window;

   /** contains list of state. */
   private final T[] state;

   /** stable and current index trigger. */
   private final AtomicBoolean index = new AtomicBoolean();

   /** state window expire time in minutes. */
   private static final int STATE_WINDOW_TIME_MINS = 5;

   /**
    * The Constructor.
    *
    * @param current current state
    * @param stable stable state
    * @param timeWindow expiration window
    */
   @SuppressWarnings("unchecked")
   public MonitorStateHandler(final T current, final T stable, final TimeValue timeWindow) {
       if ((current == null) || (stable == null) || (timeWindow == null)) {
           throw new NullPointerException("Wrong arguments for " + this.getClass().getName());
       }
       this.window = timeWindow;
       state = (T[]) Array.newInstance(current.getClass(), 2);
       state[0] = current;
       state[1] = stable;
   }

   /**
    * Creates a MonitorStateHandler with 5 minutes as default time.
    *
    * @param current current state
    * @param stable stable state
    */
   public MonitorStateHandler(final T current, final T stable) {
       this(current, stable, new TimeValue(STATE_WINDOW_TIME_MINS, TimeUnit.MINUTES));
   }

   /**
    * @return The current version of the monitor counters, should only be used for updates not reads.
    */
   public T current() {
       return state[index.get() ? 0 : 1];
   }

   /**
    * @return The stable version of the monitor counters.
    */
   public T stable() {
       return state[index.get() ? 1 : 0];
   }

   /**
    * @return Time when the instance was created in milliseconds.
    */
   public long getStartTime() {
       return startTime;
   }

   /**
    * @return Time of the last flip
    */
   public long getLastUpdate() {
       return lastFlip.get();
   }

   /**
    * @return Time window that update method will use to flip current with stable
    */
   public TimeValue getWindow() {
       return window;
   }

   /**
    * Uses getWindow to flip current with stable, should be called frequently on all read operations.
    */
   public void update() {
       long currTime = System.currentTimeMillis();
       long lastFlipTime = lastFlip.get();
       long elapsedTime = currTime - lastFlipTime;
       if (elapsedTime < window.toMillis()) {
           // return without using lock
           return;
       } else if (elapsedTime >= window.toMillis()) {
           // need lock to flip state
           final long currentTime = System.currentTimeMillis();
           synchronized (this) {
               if (lastFlip.compareAndSet(lastFlipTime, currentTime)) {
                   index.getAndSet(!index.get());
                   current().reset(stable());
               }
           }
       }
   }

   /**
    * forces the flip between current and stable state. This method should only be used for debugging or testing. update should be used instead of
    * this.
    */
   public void flip() {
       long currTime = System.currentTimeMillis();
       synchronized (this) {
           long lastFlipTime = lastFlip.get();
           if (lastFlip.compareAndSet(lastFlipTime, currTime)) {
               index.getAndSet(!index.get());
               current().reset(stable());
           }
       }
   }

   @Override
   public String toString() {
	   final StringBuilder stringBuilder = new StringBuilder();
	   return stringBuilder.append("[ Window = ").append(getWindow()).append(", StartTime = ").append(getStartTime()).append(", LastUpdate = ")
	   .append(", Stable = [").append(stable()).append("], Current = [").append(current()).append("] ]").toString();
   }
}