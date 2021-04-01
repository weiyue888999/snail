package com.github.snail.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

public class PopupCaptchaAjaxLimiterFilter extends AbstractPopupCaptchaLimiterFilter{
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private String json = "{'state':'fail','message':'please retry again later'}";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String val = filterConfig.getInitParameter("json");
		this.json = Utils.isNotEmpty(val) ? val : this.json;
	}

	@Override
	protected void doUnaccept(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,FilterChain chain) throws IOException, ServletException {
		
		httpServletResponse.setContentType(Constants.CONTENT_TYPE_JSON);
		
		log.debug("rende application/json --> "+this.json);
		
		PrintWriter printWriter = httpServletResponse.getWriter();
		printWriter.write(this.json);
	}
}
