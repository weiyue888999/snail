package com.github.snail.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.json.JSONWriter;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.logging.MDC;

/**
 * @author 		：weiguangyue
 * 
 * 抽象基础过滤器
 */
public abstract class AbstractBaseFilter implements Filter{
	
	private static final Log log = LogFactory.getLog(AbstractBaseFilter.class);
	
	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.servletContext = filterConfig.getServletContext();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		request.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF_8);
		response.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF_8);
		
		MDC.put(Constants.MDC_KEY_SESSION_ID, httpServletRequest.getSession().getId());
		
		String method = httpServletRequest.getMethod().toUpperCase();
		if(this.supportMethods().contains(method)){
			if(this.acceptDoFilterInternal(httpServletRequest,httpServletResponse,chain)) {
				this.doFilterInternal(httpServletRequest,httpServletResponse,chain);				
			}
		}else {
			httpServletResponse.setStatus(Constants.STATUS_CODE_405);
		}
		
		MDC.remove(Constants.MDC_KEY_SESSION_ID);
	}
	
	protected abstract boolean acceptDoFilterInternal(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse, FilterChain chain);
	
	protected void renderJson(HttpServletRequest request,HttpServletResponse response,Map<String,Object> resultMap) throws IOException {
		
		response.setContentType(Constants.CONTENT_TYPE_JSON);
		
		JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.writeMap(resultMap);
		
		String jsonString = jsonWriter.toString();
		log.debug("rende application/json --> "+jsonString);
		
		PrintWriter printWriter = response.getWriter();
		printWriter.write(jsonString);
	}
	
	protected void renderFileStream(HttpServletRequest request,HttpServletResponse response,String contentType,File file) throws IOException {
		
		response.setContentType(contentType);
		
		log.debug("rende "+contentType+" --> "+file);
		
		InputStream inputStream = new FileInputStream(file);
		
		try {
			Utils.copy(inputStream, response.getOutputStream());							
		}catch (Exception e) {
			log.error("render file"+ file +" error,cause "+e.getMessage());
		}finally {			
			Utils.closeQuietly(inputStream);
		}
	}
	
	protected void renderFileByte(HttpServletRequest request,HttpServletResponse response,String contentType,List<byte[]> datas,String name) throws IOException {
		
		response.setContentType(contentType);
		
		log.debug("rende "+contentType+" --> "+name);
		
		for(byte[] data : datas) {
			response.getOutputStream().write(data);			
		}
		
	}
	
	protected void renderFileByte(HttpServletRequest request,HttpServletResponse response,String contentType,byte[] data,String name) throws IOException {
		
		response.setContentType(contentType);
		
		log.debug("rende "+contentType+" --> "+name);
		
		response.getOutputStream().write(data);
		
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void destroy() {
	}
	
	protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain chain) throws IOException, ServletException;
	
	protected abstract List<String> supportMethods();
}
