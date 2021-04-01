package com.github.snail.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.ClientVerifyContext.State;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 * 
 * 通知Filter,仅popup风格使用
 */
class NotifyFilter extends AbstractCaptchaControllerFilter{
	
	private final Log log = LogFactory.getLog(this.getClass());

	public NotifyFilter(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if(!getCaptchaController().getCaptchaStyle().equals(Constants.CAPTCHA_STYLE_POPUP)) {
			
			Map<String,Object> resultMap = new HashMap<String,Object>(2);
			resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_FAIL);
			resultMap.put(Constants.RENDER_JSON_KEY_MSG, "not support");
			
			this.renderJson(request, response, resultMap);
		}else {
			
			ClientVerifyContext context = ClientVerifyContext.get(request,getCaptchaController());
			if(context == null || context.getState() == State.NOT_VERIFY) {
				//没有初始化就直接访问或没有验证成功
				response.setStatus(Constants.STATUS_CODE_500);
			}else if(context.getState() == State.VERIFIED || context.getState() == State.NOTIFIED){
				
				context.setState(State.NOTIFIED);
				
				Map<String,Object> resultMap = new HashMap<String,Object>(3);
				resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_SUCCESS);
				resultMap.put(Constants.RENDER_JSON_KEY_MSG, "");
				resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS,State.NOTIFIED.tag());
				
				this.renderJson(request, response, resultMap);
			}else {
				log.warn("undefind clientVerifyContext state , please check your code !!!");
				//暂时未定义状态
				response.setStatus(Constants.STATUS_CODE_500);
			}
		}
	}

	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_POST,Constants.HTTP_REQUEST_METHOD_GET);
	}
}
