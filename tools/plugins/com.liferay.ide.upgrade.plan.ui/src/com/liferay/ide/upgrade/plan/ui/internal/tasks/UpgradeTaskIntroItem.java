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

package com.liferay.ide.upgrade.plan.ui.internal.tasks;

import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Gregory Amerson
 */
public class UpgradeTaskIntroItem implements UpgradeTaskItem {

	public UpgradeTaskIntroItem(FormToolkit formToolkit, ScrolledForm scrolledForm, UpgradeTask upgradeTask) {
		_formToolkit = formToolkit;
		_scrolledForm = scrolledForm;
		_upgradeTask = upgradeTask;

		Section section = _formToolkit.createSection(_scrolledForm.getBody(), Section.TITLE_BAR);

		GridLayoutFactory gridLayoutFactory = GridLayoutFactory.fillDefaults();

		gridLayoutFactory.margins(0, 0);

		section.setLayout(gridLayoutFactory.create());

		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults();

		gridDataFactory.grab(true, false);

		section.setLayoutData(gridDataFactory.create());

		section.setText("Introduction");

		Composite bodyComposite = _formToolkit.createComposite(section);

		section.setClient(bodyComposite);

		_disposables.add(() -> section.dispose());

		bodyComposite.setLayout(new TableWrapLayout());

		bodyComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		_disposables.add(() -> bodyComposite.dispose());

		Label label = _formToolkit.createLabel(bodyComposite, _upgradeTask.getDescription());

		_disposables.add(() -> label.dispose());

		_buttonComposite = _formToolkit.createComposite(bodyComposite);

		GridLayout buttonGridLayout = new GridLayout(4, false);

		buttonGridLayout.marginHeight = 2;
		buttonGridLayout.marginWidth = 2;
		buttonGridLayout.verticalSpacing = 2;

		_buttonComposite.setLayout(buttonGridLayout);

		_buttonComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		_disposables.add(() -> _buttonComposite.dispose());

		Label fillLabel = _formToolkit.createLabel(_buttonComposite, null);

		GridData gridData = new GridData();

		gridData.widthHint = 16;

		fillLabel.setLayoutData(gridData);

		_disposables.add(() -> fillLabel.dispose());

		Image taskRestartImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.TASK_RESTART_IMAGE);

		ImageHyperlink taskRestartImageHyperlink = createImageHyperlink(
			_formToolkit, _buttonComposite, taskRestartImage, this, "Click to restart");

		taskRestartImageHyperlink.setEnabled(false);

		taskRestartImageHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		taskRestartImageHyperlink.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					new Job(_upgradeTask.getTitle() + " restarting.") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							return _restartTask();
						}

					}.schedule();
				}

			});

		_disposables.add(() -> taskRestartImageHyperlink.dispose());
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public void dispose() {
		for (Disposable disposable : _disposables) {
			try {
				disposable.dispose();
			}
			catch (Throwable t) {
			}
		}
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(_upgradeTask);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
	}

	private IStatus _restartTask() {
		UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);

		upgradePlanner.restartTask(_upgradeTask);

		return Status.OK_STATUS;
	}

	private Composite _buttonComposite;
	private List<Disposable> _disposables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ScrolledForm _scrolledForm;
	private final UpgradeTask _upgradeTask;

}