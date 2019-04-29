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
import java.util.List;

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
		list.add("com.lafaspot");
		File srcDir = new File(this.getClass().getClassLoader().getResource("").getPath());
		generator.generate("Test", list, "jws-touchstone.template", "./target/",
				srcDir + "/templates/touchstone/");
		Assert.assertTrue(true);
	}

	/**
	 * Test parsing the expression
	 *
	 */
	@Test
	public void testParser() {
		TemplateGenerator generator = new TemplateGenerator("test", new LogManager());
		List<String> methodList = generator.parseExpression("foo");
		Assert.assertEquals(methodList.size(), 1);
		Assert.assertTrue(methodList.contains("foo"));

		methodList = generator.parseExpression("foo/bar + fooly-barly- 100 +200");
		Assert.assertEquals(methodList.size(), 4);
		Assert.assertTrue(methodList.contains("foo"));
		Assert.assertTrue(methodList.contains("bar"));
		Assert.assertTrue(methodList.contains("fooly"));
		Assert.assertTrue(methodList.contains("barly"));

		methodList = generator.parseExpression("foo+-*/()=1234567890");
		Assert.assertEquals(methodList.size(), 1);
		Assert.assertTrue(methodList.contains("foo"));

		methodList = generator.parseExpression("foo+-*/()=http5xxs+7890");
		Assert.assertEquals(methodList.size(), 2);
		Assert.assertTrue(methodList.contains("foo"));
		Assert.assertTrue(methodList.contains("http5xxs"));

		methodList = generator.parseExpression("(http5xxs)+(http300)+5");
		Assert.assertEquals(methodList.size(), 2);
		Assert.assertTrue(methodList.contains("http300"));
		Assert.assertTrue(methodList.contains("http5xxs"));

		methodList = generator.parseExpression("(http5xxs+http300)*5");
		Assert.assertEquals(methodList.size(), 2);
		Assert.assertTrue(methodList.contains("http300"));
		Assert.assertTrue(methodList.contains("http5xxs"));
	}
}
