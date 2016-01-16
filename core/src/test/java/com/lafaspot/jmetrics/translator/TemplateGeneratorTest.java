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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lafaspot.logfast.logging.LogManager;

/**
 * MetricOutputter Test.
 *
 */
public class TemplateGeneratorTest {

	/**
	 * Test translating with existing template file.
	 * 
	 * @throws IOException
	 *             exception during operation
	 */
	@Test
	public void test() throws IOException {
		TemplateGenerator generator = new TemplateGenerator("test", new LogManager());
		List<String> list = new ArrayList<String>();
		list.add("com.lafaspot.");
		Map<String, Object> mustacheMap = new HashMap<String, Object>();
		mustacheMap.put("NameSpace", "Mail-Jedi");
		File srcDir = new File(this.getClass().getClassLoader().getResource("").getPath());
		generator.generate(mustacheMap, list, "jws-touchstone.template", "./target/", srcDir + "/templates/touchstone/");
		Assert.assertTrue(true);
	}
}
