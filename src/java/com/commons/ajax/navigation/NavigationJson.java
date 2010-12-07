/*
 * @(#)NavigationJson.java	1.0 19/08/2010
 *
 * Copyright (c) 2010, Hungroo. All rights reserved.
 */
package com.commons.ajax.navigation;

/**
 * Classe reponsavel pela geracao do objeto Json para navegacao Ajax
 * 
 * @author Bruno Alvares da Costa
 * 
 * @see ContentNavigationJson
 * @see ResourcesNavigationJson
 * @see ScriptNavigationJson
 * @see StyleNavigationJson
 */
public class NavigationJson {

	private ContentNavigationJson content;

	private ResourcesNavigationJson resources;

	private Integer errorId = 0;

	public ContentNavigationJson getContent() {
		return content;
	}

	public void setContent(ContentNavigationJson content) {
		this.content = content;
	}

	public ResourcesNavigationJson getResources() {
		return resources;
	}

	public void setResources(ResourcesNavigationJson resources) {
		this.resources = resources;
	}

	public Integer getErrorId() {
		return errorId;
	}

	public void setErrorId(Integer errorId) {
		this.errorId = errorId;
	}

}