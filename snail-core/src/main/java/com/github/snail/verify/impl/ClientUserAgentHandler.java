package com.github.snail.verify.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.verify.VerifyResult;

/**
 * 客户端代理验证
 */
class ClientUserAgentHandler extends BaseVerifyHandler{

	public ClientUserAgentHandler(CaptchaController captchaController) {
		super(captchaController);
	}

	private boolean isMatchUserAgent(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		if(userAgent != null) {
			for(String ua : this.getCaptchaController().getClientUserAgents()) {
				if(userAgent.contains(ua)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response,ClientVerifyContext verifyContext) {
		if(isMatchUserAgent(request)) {
			return DefaultVerifyResult.SuccessResult;		
		}else {
			return DefaultVerifyResult.FailResult;
		}
	}
	
	
}
