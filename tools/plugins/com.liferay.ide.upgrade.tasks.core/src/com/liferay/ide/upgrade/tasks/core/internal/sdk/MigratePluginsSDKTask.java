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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"categoryId=code", "id=migrate_plugins_sdk", "order=4", "title=Migrate Plugins SDK",
		"imagePath=icons/migrate_plugins_sdk.png",
		"description=Plugins SDK is deprecated as of Liferay Portal CE 7.x. After you’ve adapted your traditional plugin to Liferay Portal’s API, you can continue maintaining it in the Plugins SDK. In this section, you can initialize a latest SDK project, import an existing SDK project, or move the original Plugins SDK project into a Liferay Workspace."
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTask.class
)
public class MigratePluginsSDKTask extends BaseUpgradeTask {
}