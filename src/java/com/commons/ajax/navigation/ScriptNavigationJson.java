/*
 * @(#)ScriptNavigationJson.java	1.0 19/08/2010
 *
 * Copyright (c) 2010, Hungroo. All rights reserved.
 */
package com.commons.ajax.navigation;

import java.util.List;

/**
 * Classe reponsavel pela geracao do objeto Json para navegacao Ajax
 * 
 * @author Bruno Alvares da Costa
 * 
 * @see NavigationJson
 * @see ContentNavigationJson
 * @see ResourcesNavigationJson
 * @see StyleNavigationJson
 */
public class ScriptNavigationJson {

	private String code;

	private List<String> src;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<String> getSrc() {
		return src;
	}

	public void setSrc(List<String> src) {
		this.src = src;
	}

}
