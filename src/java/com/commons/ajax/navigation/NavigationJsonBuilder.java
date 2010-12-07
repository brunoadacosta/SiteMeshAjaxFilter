/*
 * @(#)NavigationJsonBuilder.java	1.0 20/08/2010
 *
 * Copyright (c) 2010, Hungroo. All rights reserved.
 */
package com.commons.ajax.navigation;

/**
 * Classe reponsavel pela geracao do NavigationJson 
 * 
 * @author Bruno Alvares da Costa
 *
 * @see NavigationJson
 */
public class NavigationJsonBuilder {

	public static NavigationJson create() {
		NavigationJson navigationJson = new NavigationJson();
		navigationJson.setContent(new ContentNavigationJson());
		navigationJson.setResources(new ResourcesNavigationJson());
		navigationJson.getResources().setJs(new ScriptNavigationJson());
		navigationJson.getResources().setCss(new StyleNavigationJson());
		
		return navigationJson;
	}
}
