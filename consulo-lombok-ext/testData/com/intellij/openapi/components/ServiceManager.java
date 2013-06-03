package com.intellij.openapi.components;

import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 17:27/03.06.13
 */
public class ServiceManager
{
	public static <T> T getService(Class<T> clazz)
	{
		return null;
	}

	public static <T> T getService(Project project, Class<T> clazz)
	{
		return null;
	}
}
