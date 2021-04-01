package com.github.snail.servlet;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 * 
 * 抽象的过滤器
 */
abstract class AbstractCaptchaControllerFilter extends AbstractBaseFilter{
	
	private static final Log log = LogFactory.getLog(AbstractCaptchaControllerFilter.class);

	private final CaptchaController captchaController;

	public AbstractCaptchaControllerFilter(CaptchaController captchaController) {
		this.captchaController = captchaController;
	}

	protected CaptchaController getCaptchaController() {
		return captchaController;
	}
	
	@Override
	protected boolean acceptDoFilterInternal(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse, FilterChain chain) {
		String instanceId = httpServletRequest.getParameter("instanceId");
		if(!this.getCaptchaController().getInstanceId().equals(instanceId)) {
			log.warn("request instanceId["+ instanceId +"] != cpatchaController instanceId["+ this.getCaptchaController().getInstanceId()  +"] for request["+httpServletRequest.getRequestURI()+"]");
			
			httpServletResponse.setStatus(400);
			
			return false;
		}else {
			return true;
		}
	}
}
