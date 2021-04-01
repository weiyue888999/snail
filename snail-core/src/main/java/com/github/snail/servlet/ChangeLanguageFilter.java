package com.github.snail.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.github.snail.i18n.ResourceBundleFactory;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

public class ChangeLanguageFilter implements Filter{
	
	private static final Log log = LogFactory.getLog(ChangeLanguageFilter.class);
	
	private String changeLanguageRequestParam = "language";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		String val = filterConfig.getInitParameter("changeLanguageRequestParam");
		this.changeLanguageRequestParam = val != null ? val : this.changeLanguageRequestParam;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		String param = request.getParameter(this.changeLanguageRequestParam);
		
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		ResourceBundleFactory.set(httpServletRequest.getSession(), param);
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
}
