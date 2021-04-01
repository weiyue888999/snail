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
import com.github.snail.i18n.ResourceBundleFactory;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.verify.VerifyHandler;
import com.github.snail.verify.VerifyResult;
import com.github.snail.verify.impl.CompositedVerifyHandler;

/**

@author ：weiguangyue

验证过滤器

 */
class VerifyFilter extends AbstractCaptchaControllerFilter{
	
	private static final Log log = LogFactory.getLog(VerifyFilter.class);
	
	private static final Map<String,Object> VERIFY_SUCCESS_RESULT_MAP = new HashMap<String,Object>(3);
	
	static {
		VERIFY_SUCCESS_RESULT_MAP.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_SUCCESS);
		VERIFY_SUCCESS_RESULT_MAP.put(Constants.RENDER_JSON_KEY_MSG, "");
		VERIFY_SUCCESS_RESULT_MAP.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS,State.VERIFIED.tag());
	}
	
	public VerifyFilter(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		VerifyHandler verifyHandler = new CompositedVerifyHandler(getCaptchaController());
		VerifyResult result = verifyHandler.verify(request, response);
		
		//验证次数递增
		ClientVerifyContext verifyContext = ClientVerifyContext.get(request,getCaptchaController());
		verifyContext.getVerify().increaseVerifyCount();
		
		String attributeName = String.format(Constants.VERIFY_RESULT_SESSION_KEY_FORMAT, getCaptchaController().getInstanceId());
		
		if(result.success()) {
			
			request.getSession().setAttribute(attributeName,"true");
			
			this.renderJson(request, response, VERIFY_SUCCESS_RESULT_MAP);
			
		}else {
			
			request.getSession().setAttribute(attributeName,"false");
			
			Map<String,Object> resultMap = new HashMap<String,Object>(4);
			resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_FAIL);
			resultMap.put(Constants.RENDER_JSON_KEY_MSG,ResourceBundleFactory.get(request.getSession()).getString(Constants.KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_FAIL));
			resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS,State.NOT_VERIFY.tag());
			
			this.renderJson(request, response, resultMap);
		}
	}

	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_POST);
	}
}
