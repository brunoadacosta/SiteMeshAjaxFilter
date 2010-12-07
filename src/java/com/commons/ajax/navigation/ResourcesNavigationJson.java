/*
 * @(#)ResourcesNavigationJson.java	1.0 19/08/2010
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
 * @see ContentNavigationJson
 * @see ScriptNavigationJson
 * @see StyleNavigationJson
 */
public class ResourcesNavigationJson {

	private ScriptNavigationJson js;

	private StyleNavigationJson css;

	public ScriptNavigationJson getJs() {
		return js;
	}

	public void setJs(ScriptNavigationJson js) {
		this.js = js;
	}

	public StyleNavigationJson getCss() {
		return css;
	}

	public void setCss(StyleNavigationJson css) {
		this.css = css;
	}

}
