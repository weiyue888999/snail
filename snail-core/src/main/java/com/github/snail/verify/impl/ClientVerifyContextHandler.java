package com.github.snail.verify.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.verify.VerifyResult;

/**
 * context验证
 */
class ClientVerifyContextHandler extends BaseVerifyHandler{

	public ClientVerifyContextHandler(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response,ClientVerifyContext verifyContext) {
		if(verifyContext == null) {
			log.warn("verify fail cause verifyContext is null !!!,mybe someone want study me");
			return DefaultVerifyResult.FailResult;
		}
		return DefaultVerifyResult.SuccessResult;
	}
}
