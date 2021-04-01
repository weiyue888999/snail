package com.github.snail.verify.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.verify.VerifyHandler;
import com.github.snail.verify.VerifyResult;

/**
 * 基础验证
 */
abstract class BaseVerifyHandler implements VerifyHandler{
	
	protected static final Log log = LogFactory.getLog(BaseVerifyHandler.class);

	private CaptchaController captchaController;
	
	public BaseVerifyHandler(CaptchaController captchaController) {
		this.captchaController = captchaController;
	}
	
	@Override
	public VerifyResult verify(HttpServletRequest request, HttpServletResponse response) {
		ClientVerifyContext verifyContext = ClientVerifyContext.get(request,this.captchaController);
		return this.doVerify(request,response,verifyContext);
	}

	protected abstract VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response, ClientVerifyContext verifyContext);

	protected CaptchaController getCaptchaController() {
		return captchaController;
	}
}
