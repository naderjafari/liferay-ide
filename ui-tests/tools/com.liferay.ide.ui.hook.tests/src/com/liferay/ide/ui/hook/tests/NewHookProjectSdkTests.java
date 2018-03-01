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

package com.liferay.ide.ui.hook.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.SdkProjectSupport;
import com.liferay.ide.ui.liferay.base.SdkSupport;
import com.liferay.ide.ui.liferay.base.TomcatSupport;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
public class NewHookProjectSdkTests extends SwtbotBase {

	public static TomcatSupport tomcat = new TomcatSupport(bot);

	@ClassRule
	public static RuleChain chain = RuleChain.outerRule(tomcat).around(new SdkSupport(bot, tomcat));

	@Test
	public void createSampleProject() {
		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareHookSdk(project.getNameHook());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getNameHook());

		viewAction.project.closeAndDelete(project.getNameHook());
	}

	@Rule
	public SdkProjectSupport project = new SdkProjectSupport(bot);

}