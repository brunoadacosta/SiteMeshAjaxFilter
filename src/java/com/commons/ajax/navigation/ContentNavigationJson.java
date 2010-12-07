/*
 * @(#)ContentNavigationJson.java	1.0 19/08/2010
 *
 * Copyright (c) 2010, Hungroo. All rights reserved.
 */
package com.commons.ajax.navigation;

/**
 * Classe reponsavel pela geracao do objeto Json para navegacao Ajax
 * 
 * @author Bruno Alvares da Costa
 * 
 * @see NavigationJson
 * @see ResourcesNavigationJson
 * @see ScriptNavigationJson
 * @see StyleNavigationJson
 */
/**
 * @author Bruno Alvares da Costa
 * 
 */
public class ContentNavigationJson {

	private String title;

	private String html;

	private String alias;

	private String breadcrumb;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}
}
