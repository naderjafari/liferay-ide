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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;
import com.liferay.ide.functional.module.deploy.base.DeployModuleGradleTomcat7xBase;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 * @author Lily Li
 */
@Ignore("ignore because blade 3.10.0 does not support the creation of gradle standalone")
public class DeployModuleGradleTomcat70Tests extends DeployModuleGradleTomcat7xBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if (PureTomcat70Support.isNot(server)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Ignore("ignore because blade 3.10.0 remove activator")
	@Test
	public void deployActivator() {
		super.deployActivator();
	}

	@Test
	public void deployApi() {
		super.deployApi();
	}

	@Test
	public void deployControlMenuEntry() {
		super.deployControlMenuEntry();
	}

	@Test
	public void deployFormField() {
		super.deployFormField();
	}

	@Test
	public void deployPanelApp() {
		super.deployPanelApp();
	}

	@Test
	public void deployPortletConfigurationIcon() {
		super.deployPortletConfigurationIcon();
	}

	@Test
	public void deployPortletProvider() {
		super.deployPortletProvider();
	}

	@Test
	public void deployPortletToolbarContributor() {
		super.deployPortletToolbarContributor();
	}

	@Test
	public void deployRest() {
		super.deployRest();
	}

	@Test
	public void deployService() {
		super.deployService();
	}

	@Test
	public void deployServiceWrapper() {
		super.deployServiceWrapper();
	}

	@Test
	public void deploySimulationPanelEntry() {
		super.deploySimulationPanelEntry();
	}

	@Test
	public void deployTemplateContextContributor() {
		super.deployTemplateContextContributor();
	}

	@Test
	public void deployThemeContributor() {
		super.deployThemeContributor();
	}

	@Test
	public void deployWarHook() {
		super.deployWarHook();
	}

	@Test
	public void deployWarMvcPortlet() {
		super.deployWarMvcPortlet();
	}

	@Override
	protected String getVersion() {
		return "7.0";
	}

}