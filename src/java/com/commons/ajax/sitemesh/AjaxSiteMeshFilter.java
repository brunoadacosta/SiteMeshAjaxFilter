/*
 * @(#)AjaxSiteMeshFilter.java	1.0 19/08/2010
 *
 * Copyright (c) 2010, Hungroo. All rights reserved.
 */

package com.commons.ajax.sitemesh;

import info.codesaway.util.regex.Matcher;
import info.codesaway.util.regex.Pattern;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.commons.ajax.navigation.ContentNavigationJson;
import com.commons.ajax.navigation.NavigationJson;
import com.commons.ajax.navigation.NavigationJsonBuilder;
import com.commons.ajax.navigation.ResourcesNavigationJson;
import com.commons.ajax.navigation.ScriptNavigationJson;
import com.commons.ajax.navigation.StyleNavigationJson;
import com.google.gson.GsonBuilder;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.sitemesh.compatability.DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;
import com.opensymphony.sitemesh.webapp.ContainerTweaks;
import com.opensymphony.sitemesh.webapp.ContentBufferingResponse;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * Classe responsavel por filtrar requisicoes ajax e serializar um objeto Json
 * 
 * @author Bruno Alvares da Costa
 * 
 * @see NavigationJson
 * @see ContentNavigationJson
 * @see ResourcesNavigationJson
 * @see ScriptNavigationJson
 * @see StyleNavigationJson
 */
public class AjaxSiteMeshFilter implements Filter {

	private FilterConfig filterConfig;
	private ContainerTweaks containerTweaks;

	private final String ER_JS_KEY = "((?<html>(href|src)\\s*=\\s*\")|(?<css>url\\())(?<url>.*\\.js?)(?(html)\"|\\))";
	private final String ER_CSS_KEY = "((?<html>(href|src)\\s*=\\s*\")|(?<css>url\\())(?<url>.*\\.css?)(?(html)\"|\\))";
	private final String ER_QUOTED_STRING = "\"((\\\")|[^\"(\\\")])+\"";
	private final String ER_STYLE_KEY = "<style [.\\w\\W]*?</style>";
	private final String ER_SCRIPT_KEY = "(<script[^>]*?>)(.*?)(</script>)";

	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		containerTweaks = new ContainerTweaks();
	}

	public void destroy() {
		filterConfig = null;
		containerTweaks = null;
	}

	/**
	 * Main method of the Filter.
	 * 
	 */
	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;
		ServletContext servletContext = filterConfig.getServletContext();
		if (!isAjax(request)) {
			chain.doFilter(request, response);
			return;
		}
		SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);
		ContentProcessor contentProcessor = initContentProcessor(webAppContext);
		try {

			Content content = obtainContent(contentProcessor, webAppContext, request, response, chain);

			if (content == null) {
				return;
			}

			// if (isAjax(request)) {
			Content2HTMLPage page = new Content2HTMLPage(content, request);
			response.setContentType("application/x-javascript");

			PrintWriter out = response.getWriter();
			HtmlCompressor compressor = new HtmlCompressor();
			NavigationJson json = NavigationJsonBuilder.create();
			compressor.setRemoveIntertagSpaces(false);

			String head = compressor.compress(page.getHead());

			json.getContent().setTitle(page.getTitle());
			json.getContent().setAlias(page.getProperty("body.navigationAlias"));
			json.getContent().setBreadcrumb(page.getProperty("body.breadcrumb"));

			compressor.setRemoveQuotes(true);
			String body = page.getBody();

			Pattern patternJs = Pattern.compile(ER_JS_KEY, Pattern.DOTNET_NUMBERING);
			Pattern patternCss = Pattern.compile(ER_CSS_KEY, Pattern.DOTNET_NUMBERING);
			Pattern patternQuoted = Pattern.compile(ER_QUOTED_STRING, Pattern.DOTNET_NUMBERING);
			Pattern patternStyle = Pattern.compile(ER_STYLE_KEY, Pattern.DOTNET_NUMBERING);
			Pattern patternScript = Pattern.compile(ER_SCRIPT_KEY, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

			Matcher matcher = null;
			Matcher matcherQuoted = null;
			Matcher matcherScript = null;

			matcher = patternJs.matcher(head);
			List<String> lstJS = null;

			if (matcher.groupCount() > 0) {
				lstJS = new ArrayList<String>(matcher.groupCount());
			}

			while (matcher.find()) {
				matcherQuoted = patternQuoted.matcher(matcher.group());
				while (matcherQuoted.find()) {
					lstJS.add(matcherQuoted.group().replace("\"", ""));
				}
			}

			if (lstJS != null) {
				json.getResources().getJs().setSrc(lstJS);
			}

			matcherScript = patternScript.matcher(head);
			StringBuilder strBuilder = new StringBuilder();

			while (matcherScript.find()) {
				matcher = patternJs.matcher(matcherScript.group());
				if (!matcher.find()) {
					strBuilder.append(matcherScript.group().replace("<script type=\"text/javascript\">", "")
							.replace("</script>", ""));
				}
			}

			matcherScript = patternScript.matcher(body);

			while (matcherScript.find()) {
				strBuilder.append(matcherScript.group().replace("<script type=\"text/javascript\">", "")
						.replace("</script>", ""));
			}

			body = matcherScript.replaceAll("");

			json.getContent().setHtml(compressor.compress(body));
			//TODO: add js compress (verify bug yuicompressor JavascriptCompressor.java)
			if (strBuilder.length() > 0) {
				json.getResources().getJs()
						.setCode(strBuilder.toString().replace("\r", "").replace("\n", "").replace("\t", ""));
			}

			matcher = patternCss.matcher(head);

			List<String> lstCss = null;

			if (matcher.groupCount() > 0) {
				lstCss = new ArrayList<String>(matcher.groupCount());
			}

			while (matcher.find()) {
				matcherQuoted = patternQuoted.matcher(matcher.group());
				while (matcherQuoted.find()) {
					lstCss.add(matcherQuoted.group().replace("\"", ""));
				}
			}

			if (lstCss != null) {
				json.getResources().getCss().setSrc(lstCss);
			}

			matcher = patternStyle.matcher(head);
			strBuilder = new StringBuilder();

			while (matcher.find()) {
				strBuilder.append(matcher.group().replace("<style type=\"text/css\">", "").replace("</style>", ""));
			}

			StringReader in = new StringReader(strBuilder.toString());
			CssCompressor cssCompressor = new CssCompressor(in);
			StringWriter result = new StringWriter();
			cssCompressor.compress(result, -1);

			if (strBuilder.length() > 0) {
				json.getResources().getCss().setCode(result.toString());
			}

			in.close();
			in = null;
			result.close();
			result = null;

			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls().disableHtmlEscaping();

			out.print(builder.create().toJson(json));
		} catch (IllegalStateException e) {
			if (!containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()) {
				ReponseError(response);
			}
		} catch (RuntimeException e) {
			ReponseError(response);
		} catch (Exception e) {
			ReponseError(response);
		}

	}

	private void ReponseError(HttpServletResponse response) {
		NavigationJson navigationJson = NavigationJsonBuilder.create();
		navigationJson.setErrorId(1);
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls().disableHtmlEscaping();
		try {
			response.getWriter().print(builder.create().toJson(navigationJson));
		} catch (IOException e) {
			throw new RuntimeException();
		}

	}

	protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext) {
		// TODO: Remove heavy coupling on horrible SM2 Factory
		Factory factory = Factory.getInstance(new Config(filterConfig));
		return new PageParser2ContentProcessor(factory);
	}

	protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
		// TODO: Remove heavy coupling on horrible SM2 Factory
		Factory factory = Factory.getInstance(new Config(filterConfig));
		return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
	}

	/**
	 * Continue in filter-chain, writing all content to buffer and parsing into
	 * returned {@link com.opensymphony.module.sitemesh.Page} object. If
	 * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is
	 * returned.
	 */
	private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext,
			HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, contentProcessor,
				webAppContext);
		chain.doFilter(request, contentBufferingResponse);
		webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
		return contentBufferingResponse.getContent();
	}

	private boolean isAjax(HttpServletRequest request) {
		return RequestConstants.AJAX_KEY.equals(request.getHeader("X-Requested-With"))
				&& request.getParameter(RequestConstants.AJAX_PARAM_KEY) != null;
	}
}
