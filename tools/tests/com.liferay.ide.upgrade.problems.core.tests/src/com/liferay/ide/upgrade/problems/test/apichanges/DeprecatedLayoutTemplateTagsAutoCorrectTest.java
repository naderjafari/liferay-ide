/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Seiphon Wang
 */
public class DeprecatedLayoutTemplateTagsAutoCorrectTest {

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();

		File testFile = new File(tempFolder, "liferay-layout-templates.xml");

		tempFolder.deleteOnExit();

		File originalTestfile = new File("tests/files/liferay-layout-templates.xml");

		Files.copy(originalTestfile.toPath(), testFile.toPath());

		List<UpgradeProblem> problems = null;
		FileMigrator migrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs = _context.getServiceReferences(FileMigrator.class, null);

		for (ServiceReference<FileMigrator> mref : mrefs) {
			migrator = _context.getService(mref);

			Class<?> clazz = migrator.getClass();

			if (clazz.getName().contains("DeprecatedLayoutTemplateTags")) {
				problems = migrator.analyze(testFile);

				break;
			}
		}

		Assert.assertEquals("", 1, problems.size());

		int problemsFixed = ((AutoFileMigrator)migrator).correctProblems(testFile, problems);

		Assert.assertEquals("", 1, problemsFixed);

		problems = migrator.analyze(testFile);

		Assert.assertEquals("", 0, problems.size());
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();
}
