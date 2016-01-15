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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.lafaspot.common.util.AnnotationClassScanner;
import com.lafaspot.jmetrics.annotation.MetricCheck;
import com.lafaspot.jmetrics.annotation.MetricClass;
import com.lafaspot.logfast.logging.LogContext;
import com.lafaspot.logfast.logging.LogManager;
import com.lafaspot.logfast.logging.Logger;

/**
 * Translates the template file to the formatted files based on the parameter
 * passed in.
 *
 */
public class TemplateGenerator {
	/** Mustache Factory. */
	private final MustacheFactory mf;

	/** Logger class. */
	private final Logger logger;

	/** LogManager. */
	private final LogManager logManager;

	private final String metricClassApplication;

	/**
	 * Constructor for TemplateGenerator which outputs the corresponding files
	 * based on the templateType and metricType.
	 * 
	 * @param metricClassApplication
	 *            metricClass application name
	 * @param logManager
	 *            log manager
	 */
	public TemplateGenerator(final String metricClassApplication, final LogManager logManager) {
		mf = new DefaultMustacheFactory();
		this.metricClassApplication = metricClassApplication;
		this.logManager = logManager;
		LogContext context = new TemplateGeneratorContext(this.getClass().getName());
		logger = logManager.getLogger(context);
	}

	/**
	 * Generate the files based on the templates.
	 * 
	 * @param mustacheScope
	 *            map where mustache references for substitution.
	 * @param allowFilters
	 *            list of filters allowed
	 * @param outputFileName
	 *            output filename
	 * @param outputDirectory
	 *            output directory
	 * @param templateSrcDir
	 *            source of the template source directory
	 * @throws IOException
	 *             throw when IO exception occurs
	 */
	public void generate(final Map<String, Object> mustacheScope, final List<String> allowFilters,
			final String outputFileName, final String outputDirectory, final String templateSrcDir) throws IOException {
		if (!Files.isDirectory(Paths.get(outputDirectory))) {
			throw new IllegalArgumentException("Directory: " + outputDirectory + " does not exist.");
		}
		if (!Files.isDirectory(Paths.get(templateSrcDir))) {
			throw new IllegalArgumentException("Directory: " + templateSrcDir + " does not exist.");
		}
		Set<Class<?>> annotatedClazzez = null;
		final AnnotationClassScanner<MetricClass> scanClasses = new AnnotationClassScanner<MetricClass>(
				MetricClass.class, allowFilters, this.logManager);
		annotatedClazzez = scanClasses.scanAnnotatedClasses();

		Path outputFilePath = Paths.get(outputDirectory + "/" + outputFileName);
		File outputFile = new File(outputFilePath.toUri());

		if (outputFile.exists()) {
			outputFile.delete();
		}
		outputFile.createNewFile();

		writeHeader(templateSrcDir, outputFile, mustacheScope);

		for (final Class<?> annotatedClazz : annotatedClazzez) {
			final MetricClass metricClass = annotatedClazz.getAnnotation(MetricClass.class);
			if (!Arrays.asList(metricClass.applications()).contains(metricClassApplication)) {
				continue;
			}
			String metricClassName = metricClass.name();
			if (!metricClass.enable() || metricClassName == null || metricClassName.isEmpty()) {
				continue;
			}
			mustacheScope.put("MetricClass", metricClassName);
			mustacheScope.put("MetricClassLowerCase", metricClassName.toLowerCase());

			final List<Method> methods = new ArrayList<Method>(Arrays.asList(annotatedClazz.getDeclaredMethods()));

			for (final Method method : methods) {
				if (method.isAnnotationPresent(MetricCheck.class)) {
					MetricCheck metricCheck = method.getAnnotation(MetricCheck.class);

					if (metricCheck == null || !metricCheck.enable()) {
						continue;
					}
					if (metricCheck.type() == null || metricCheck.type().isEmpty()) {
						continue;
					}
					// Removing the "get" in the method name
					String methodName = removeGetPrefix(method);
					mustacheScope.put("MethodName", methodName);
					mustacheScope.put("MethodNameLowerCase", methodName.toLowerCase());

					File templateFile = getTemplateFile(metricCheck.type(), templateSrcDir);
					if (templateFile.exists()) {
						outputToFile(templateFile, outputFile, mustacheScope);
					}
				}
			}
		}

		writeFooter(templateSrcDir, outputFile, mustacheScope);

	}

	/**
	 * Write the header to the output file based on the template type specified.
	 * 
	 * @param outputFile
	 *            output file
	 * @param mustacheScope
	 *            map where mustache references for substitution.
	 * @param templateType
	 *            type of template
	 * 
	 * @throws FileNotFoundException
	 *             throw when file is not found
	 */
	private void writeHeader(final String templateSrcDir, final File outputFile,
			final Map<String, Object> mustacheScope) throws FileNotFoundException {
		final File headerTemplate = getTemplateFile("header", templateSrcDir);
		if (headerTemplate.exists()) {
			outputToFile(headerTemplate, outputFile, mustacheScope);
		}
	}

	/**
	 * Write the footer to the output file based on the template type specified.
	 * 
	 * @param outputFile
	 *            output file
	 * @param mustacheScope
	 *            map where mustache references for substitution.
	 * @param templateType
	 *            type of template
	 * 
	 * @throws FileNotFoundException
	 *             throw when file is not found
	 */
	private void writeFooter(final String templateSrcDir, final File outputFile,
			final Map<String, Object> mustacheScope) throws FileNotFoundException {
		final File footerTemplate = getTemplateFile("footer", templateSrcDir);
		if (footerTemplate.exists()) {
			outputToFile(footerTemplate, outputFile, mustacheScope);
		}
	}

	/**
	 * To retrieve the template file based on the metric type and template type.
	 * 
	 * @param templateType
	 *            template type
	 * @param templateSrcDir
	 *            template source directory
	 * @return template file
	 * @throws FileNotFoundException
	 *             throw when file is not found
	 */
	private File getTemplateFile(final String templateType, final String templateSrcDir) throws FileNotFoundException {
		File template = null;
		switch (templateType) {
		case "header":
			template = new File(templateSrcDir + "/header.template");
			break;
		case "footer":
			template = new File(templateSrcDir + "/footer.template");
			break;
		default:
			// Check if there are any non-default ones
			template = new File(templateSrcDir + "/" + templateType + ".template");
			break;
		}
		
		if (template == null || !template.exists()){
			throw new FileNotFoundException(
					"Template file " + templateType + ".template not found in " + templateSrcDir);
		}
		return template;
	}

	/**
	 * Based on the template file and the mustache variables. Output the result
	 * by appending to the outputFile.
	 * 
	 * @param template
	 *            location of the template file.
	 * @param outputFile
	 *            output file.
	 * @param mustacheScope
	 *            map where mustache references for substitution.
	 */
	private void outputToFile(final File template, final File outputFile, final Map<String, Object> mustacheScope) {
		BufferedWriter bw = null;
		Mustache mustache = mf.compile(template.getPath());
		try {
			bw = new BufferedWriter(new FileWriter(outputFile, true));
			mustache.execute(bw, mustacheScope).flush();
			bw.newLine();
		} catch (IOException e) {
			logger.error("Error occurs in writing/reading template/output file", e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("Error occurs in writing/reading template/output file", e);
				}
			}
		}
	}

	/**
	 * Remove the "get" word in the method name.
	 * 
	 * @param method
	 *            method name
	 * @return method name without the "get" word if it exists.
	 */
	private String removeGetPrefix(final Method method) {
		String methodName = method.getName();
		if (methodName.startsWith("get") && methodName.length() > 3) {
			methodName = method.getName().substring(3, methodName.length());
		}
		return methodName;
	}

}