package com.github.snail.verify.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.verify.VerifyResult;

/**
 * rtk验证
 */
public class RtkHandler extends BaseVerifyHandler{

	public RtkHandler(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response,ClientVerifyContext verifyContext) {
		String rtk = request.getParameter("rtk");
		if(!verifyContext.getRtk().equals(rtk)) {
			return DefaultVerifyResult.FailResult;
		}
		return DefaultVerifyResult.SuccessResult;
	}
}
