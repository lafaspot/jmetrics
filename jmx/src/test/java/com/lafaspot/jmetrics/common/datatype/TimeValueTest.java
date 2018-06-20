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

package com.lafaspot.jmetrics.common.datatype;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests of the {@linkplain TimeValue}.
 * 
 * @author manish211
 *
 */
public class TimeValueTest {

    /**
     * Test to verify comparisons.
     */
    @Test
    public void comparisons() {
        Assert.assertTrue((new TimeValue(1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) == 0);
        Assert.assertTrue((new TimeValue(1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(1000, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) == 0);
        Assert.assertTrue((new TimeValue(999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(999, TimeUnit.NANOSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(1001, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(1999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(107000, TimeUnit.DAYS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(106000, TimeUnit.DAYS)).compareTo(new TimeValue(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) < 0);

        Assert.assertTrue((new TimeValue(-1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1000, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(999, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1001, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-107000, TimeUnit.DAYS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-106000, TimeUnit.DAYS)).compareTo(new TimeValue(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) < 0);

        Assert.assertTrue((new TimeValue(-1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.NANOSECONDS)) == 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.MICROSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(-1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1000, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.MICROSECONDS)) == 0);
        Assert.assertTrue((new TimeValue(-999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.MICROSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(-1, TimeUnit.MICROSECONDS)).compareTo(new TimeValue(-999, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1001, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-1999, TimeUnit.NANOSECONDS)).compareTo(new TimeValue(-1, TimeUnit.MICROSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-107000, TimeUnit.DAYS)).compareTo(new TimeValue(-1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-107000, TimeUnit.DAYS)).compareTo(new TimeValue(1, TimeUnit.NANOSECONDS)) < 0);
        Assert.assertTrue((new TimeValue(-106000, TimeUnit.DAYS)).compareTo(new TimeValue(Long.MIN_VALUE, TimeUnit.NANOSECONDS)) > 0);

        // Verify that overflow and underflow are caught.
        Assert.assertTrue((new TimeValue(107000, TimeUnit.DAYS)).compareTo(new TimeValue(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) > 0);
        Assert.assertTrue((new TimeValue(-107000, TimeUnit.DAYS)).compareTo(new TimeValue(Long.MIN_VALUE, TimeUnit.NANOSECONDS)) < 0);
    }

    /**
     * Verifies that we can parse zero and units.
     */
    @Test
    public void testZeroValues() {
        final String[] vals = { "0ns", "0us", "0ms", "0s", "0m", "0h", "0d" };
        final TimeUnit[] tus = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
                TimeUnit.HOURS, TimeUnit.DAYS };
        for (int index = 0; index < vals.length; index++) {
            final TimeValue tv = TimeValue.parse(vals[index]);
            Assert.assertEquals(tv.getValue(), 0L);
            Assert.assertEquals(tv.getTimeUnit(), tus[index]);
        }
    }

    /**
     * Verifies that we can parse nonzero and units.
     */
    @Test
    public void testNonZeros() {
        final String[] vals = { "150ns", "150us", "150ms", "150s", "150m", "150h", "150d" };
        final TimeUnit[] tus = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
                TimeUnit.HOURS, TimeUnit.DAYS };
        for (int index = 0; index < vals.length; index++) {
            final TimeValue tv = TimeValue.parse(vals[index]);
            Assert.assertEquals(tv.getValue(), 150L);
            Assert.assertEquals(tv.getTimeUnit(), tus[index]);
        }
    }

    /**
     * Verifies that we can convert units to other units.
     */
    @Test
    public void testConvertValues() {
        final String[] vals = { "150ns", "150us", "150ms", "150s", "150m", "150h", "150d" };
        final long[][] longs = { { 0, 0, 0, 0, 0, 0 }, // 150ns
                { 150, 0, 0, 0, 0, 0 }, // 150us
                { 150000, 150, 0, 0, 0, 0 }, // 150ms
                { 150000000, 150000, 150, 2, 0, 0 }, // 150s
                { 9000000000L, 9000000, 9000, 150, 2, 0 }, // 150m
                { 540000000000L, 540000000, 540000, 9000, 150, 6 }, // 150h
                { 12960000000000L, 12960000000L, 12960000, 216000, 3600, 150 }, // 150d
        };
        for (int index = 0; index < vals.length; index++) {
            final TimeValue tv = TimeValue.parse(vals[index]);
            Assert.assertEquals(tv.toMicros(), longs[index][0]);
            Assert.assertEquals(tv.toMillis(), longs[index][1]);
            Assert.assertEquals(tv.toSeconds(), longs[index][2]);
            Assert.assertEquals(tv.toMinutes(), longs[index][3]);
            Assert.assertEquals(tv.toHours(), longs[index][4]);
            Assert.assertEquals(tv.toDays(), longs[index][5]);
        }
    }
}