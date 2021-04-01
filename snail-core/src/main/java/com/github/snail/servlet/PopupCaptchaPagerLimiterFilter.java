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

/**
 * @author 		：weiguangyue
 * 
 * 弹出式图片验证码拦截器,若没有成功验证之前旧访问目标路径，则直接爆出500错误
 */
public class PopupCaptchaPagerLimiterFilter extends AbstractPopupCaptchaLimiterFilter{
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private String html = "{'state':'fail','message':'please retry again later'}";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String val = filterConfig.getInitParameter("html");
		this.html = Utils.isNotEmpty(val) ? val : this.html;
	}

	@Override
	protected void doUnaccept(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,FilterChain chain) throws IOException, ServletException {
		
		httpServletResponse.setContentType(Constants.CONTENT_TYPE_HTML);
		
		log.debug("rende application/json --> "+this.html);
		
		PrintWriter printWriter = httpServletResponse.getWriter();
		printWriter.write(this.html);
	}
}
