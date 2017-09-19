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

package com.lafaspot.jmetrics.translator;

import com.lafaspot.jmetrics.annotation.Alert;
import com.lafaspot.jmetrics.annotation.Metric;
import com.lafaspot.jmetrics.annotation.MetricCheck;
import com.lafaspot.jmetrics.annotation.MetricClass;

/**
 * Monitor BarMessage for test.
 *
 */
@MetricClass(enable = true, name = "BarMessageMonitor", applications = "test")
public class BarMessageMonitor {

	/**
	 * Test Failure.
	 * 
	 * @return failure
	 */
	@Metric(enable = true, type = "latency")
	@Alert(max = 1000, min = 500)
	@MetricCheck(type = "ratio", expression = "BarFailures / BarCount")
	public long getBarFailures() {
		return 500;
	}

	/**
	 * Test Failure.
	 * 
	 * @return failure
	 */
	@Metric(enable = true, type = "latency")
	@Alert(max = 1000, min = 500)
	@MetricCheck(type = "ratio", expression = "BarCountFailures")
	public long getBarCountFailures() {
		return 500;
	}

	/**
	 * Test disabled count.
	 * 
	 * @return disabled count
	 */
	@Metric(enable = false, type = "count")
	@Alert(max = 2000, min = 90)
	public long getBarDisabledCount() {
		return 50;
	}

	/**
	 * Test bar count.
	 * 
	 * @return bar count
	 */
	@Metric(enable = true, type = "count")
	@Alert(max = 200, min = 100)
	public long getBarCount() {
		return 100;
	}

	/**
	 * Test bar latency.
	 *
	 * @return bar latency
	 */
	@Metric(enable = true, type = "latency")
	@MetricCheck(enable = true, type = "latency")
	@Alert(max = 200, min = 100)
	public long getBarLatency() {
		return 100;
	}
}
