/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorations;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorationsManager;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public abstract class PluginClasspathContainer implements IClasspathContainer {

	protected static ClasspathDecorationsManager cpDecorations;

	protected static final String SEPARATOR = "!";

	static {
		cpDecorations = new ClasspathDecorationsManager(LiferayServerCorePlugin.PLUGIN_ID);
	}

	public static String getDecorationManagerKey(IProject project, String container) {
		return project.getName() + SEPARATOR + container;
	}

	static ClasspathDecorationsManager getDecorationsManager() {
		return cpDecorations;
	}

	protected IClasspathEntry[] classpathEntries;

	protected IPath path;

	protected IPath portalDir;

	protected IJavaProject javaProject;

	protected IFile pluginPackageFile;

	public PluginClasspathContainer(IPath containerPath, IJavaProject project, IPath portalDir) {
		this.path = containerPath;
		this.javaProject = project;
		this.portalDir = portalDir;
	}

	public IClasspathEntry[] getClasspathEntries() {
		if (this.classpathEntries == null) {
			List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

			if (this.portalDir != null) {
				for (String pluginJar : getPortalJars()) {
					entries.add(createPortalJarClasspathEntry(pluginJar));
				}

				for (String pluginPackageJar : getPortalDependencyJars()) {
					entries.add(createPortalJarClasspathEntry(pluginPackageJar));
				}			}

			for (String context : getRequiredDeploymentContexts()) {
				IClasspathEntry entry = createContextClasspathEntry(context);

				if (entry != null) {
					entries.add(entry);
				}
			}

			this.classpathEntries = entries.toArray(new IClasspathEntry[entries.size()]);
		}

		return this.classpathEntries;
	}

	public abstract String getDescription();

	public int getKind() {
		return K_APPLICATION;
	}

	public IPath getPath() {
		return this.path;
	}

	public IPath getPortalDir() {
		return portalDir;
	}

	protected IClasspathEntry createContextClasspathEntry(String context) {
		IClasspathEntry entry = null;

		// first look for jar in a project with same name as context
		// if it is not available, look for a project in deployed
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for (IProject project : projects) {
			if (project.getName().equals(context)) {
				// check to see if the project that has a service jar

				IFolder docroot = ProjectUtil.getDocroot(project);

				IFile serviceJar = docroot.getFile("WEB-INF/lib/" + project.getName() + "-service.jar");
				IFolder serviceFolder = docroot.getFolder("WEB-INF/service");

				if (serviceJar.exists()) {
					entry =
						createClasspathEntry(
							serviceJar.getFullPath(), serviceFolder.exists() ? serviceFolder.getFullPath() : null);

					break;
				}
			}
		}

		if (entry == null) {
			IProject project = this.javaProject.getProject();

			SDK sdk = ProjectUtil.getSDK(project);

			IPath sdkLocation = sdk.getLocation();

			String type =
				ProjectUtil.isPortletProject(project) ? "portlets" : ProjectUtil.isHookProject(project)
					? "hooks" : ProjectUtil.isExtProject(project) ? "ext" : "";

			IPath serviceJar =
				sdkLocation.append(type).append(context).append("docroot/WEB-INF/lib").append(context + "-service.jar");

			if (serviceJar.toFile().exists()) {
				IPath servicePath = serviceJar.removeLastSegments(2).append("service");

				entry = createClasspathEntry(serviceJar, servicePath.toFile().exists() ? servicePath : null);
			}
		}

		return entry;
	}

	protected IClasspathEntry createPortalJarClasspathEntry(String portalJar) {
		IPath entryPath = this.portalDir.append("/WEB-INF/lib/" + portalJar);

		return createClasspathEntry(entryPath, null);
	}

	protected IClasspathEntry createClasspathEntry(IPath entryPath, IPath sourcePath) {
		IPath sourceRootPath = null;
		IAccessRule[] rules = new IAccessRule[] {};
		IClasspathAttribute[] attrs = new IClasspathAttribute[] {};

		final ClasspathDecorations dec =
			cpDecorations.getDecorations(
				getDecorationManagerKey(javaProject.getProject(), getPath().toString()), entryPath.toString());

		if (dec != null) {
			sourcePath = dec.getSourceAttachmentPath();
			sourceRootPath = dec.getSourceAttachmentRootPath();
			attrs = dec.getExtraAttributes();
		}

		return JavaCore.newLibraryEntry(entryPath, sourcePath, sourceRootPath, rules, attrs, false);
	}

	protected IClasspathEntry findSuggestedEntry(IPath jarPath, IClasspathEntry[] suggestedEntries) {
		// compare jarPath to an existing entry
		if (jarPath != null && (!CoreUtil.isNullOrEmpty(jarPath.toString())) &&
			(!CoreUtil.isNullOrEmpty(suggestedEntries))) {
			int matchLength = jarPath.segmentCount();

			for (IClasspathEntry suggestedEntry : suggestedEntries) {
				IPath suggestedPath = suggestedEntry.getPath();
				IPath pathToMatch =
					suggestedPath.removeFirstSegments(suggestedPath.segmentCount() - matchLength).setDevice(null).makeAbsolute();
				if (jarPath.equals(pathToMatch)) {
					return suggestedEntry;
				}
			}
		}

		return null;
	}

	protected String getPropertyValue(String key, IFile propertiesFile) {
		String retval = null;

		try {
			Properties props = new Properties();

			props.load(pluginPackageFile.getContents());

			retval = props.getProperty(key, "");
		}
		catch (Exception e) {
		}

		return retval;
	}

	protected abstract String[] getPortalJars();

	protected IFile getPluginPackageFile() {
		if (pluginPackageFile == null || (!pluginPackageFile.exists())) {
			IVirtualComponent comp = ComponentCore.createComponent(this.javaProject.getProject());

			if (comp != null) {
				IContainer resource = comp.getRootFolder().getUnderlyingFolder();

				if (resource instanceof IFolder) {
					IFolder webroot = (IFolder) resource;

					pluginPackageFile =
						webroot.getFile("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);

					if (!pluginPackageFile.exists()) {
						// IDE-226 the file may be missing because we are in an ext plugin which has a different layout
						// check for ext-web in the path to the docroot

						if (webroot.getFullPath().toPortableString().endsWith("WEB-INF/ext-web/docroot")) {
							// look for packages file in first docroot
							IPath parentDocroot = webroot.getFullPath().removeFirstSegments(1).removeLastSegments(3);
							IFolder parentWebroot = this.javaProject.getProject().getFolder(parentDocroot);

							if (parentWebroot.exists()) {
								pluginPackageFile =
									parentWebroot.getFile("WEB-INF/" +
										ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);
							}						}
					}
				}
			}
		}

		return pluginPackageFile;
	}
	
	protected String[] getRequiredDeploymentContexts() {
		String[] jars = new String[0];

		IFile pluginPackageFile = getPluginPackageFile();

		try {
			String context = getPropertyValue("required-deployment-contexts", pluginPackageFile);

			String[] split = context.split(",");

			if (split.length > 0 && !(CoreUtil.isNullOrEmpty(split[0]))) {
				return split;
			}
		}
		catch (Exception e) {
		}

		return jars;
	}

	protected String[] getPortalDependencyJars() {
		String[] jars = new String[0];

		IFile pluginPackageFile = getPluginPackageFile();

		try {
			String deps = getPropertyValue("portal-dependency-jars", pluginPackageFile);

			String[] split = deps.split(",");

			if (split.length > 0 && !(CoreUtil.isNullOrEmpty(split[0]))) {
				return split;
			}
		}
		catch (Exception e) {
		}
		return jars;
	}

}
