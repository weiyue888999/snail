package com.github.snail.verify.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.ClientVerifyContext.State;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.verify.VerifyHandler;
import com.github.snail.verify.VerifyResult;

/**
 * 组合验证
 */
public class CompositedVerifyHandler extends BaseVerifyHandler{
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private List<VerifyHandler> handlers = new ArrayList<VerifyHandler>(4);
	
	public CompositedVerifyHandler(CaptchaController captchaController) {
		super(captchaController);
		
		this.handlers.add(new ClientVerifyContextHandler(captchaController));
		//浏览器检测
		if(this.getCaptchaController().isMatchClientUserAgent()) {
			this.handlers.add(new ClientUserAgentHandler(captchaController));
		}
		//检测rtk
		if(this.getCaptchaController().isMatchRtk()) {
			this.handlers.add(new RtkHandler(captchaController));
		}
		
		//and son on
		// ...
		
		this.handlers.add(new TraceHandler(captchaController));
	}

	@Override
	public VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response,ClientVerifyContext verifyContext) {
		
		for(VerifyHandler h : this.handlers) {
			VerifyResult result = h.verify(request, response);
			if(!result.success()) {
				return result;
			}
		}
		verifyContext.setState(State.VERIFIED);
		log.debug("verify success !");
		return DefaultVerifyResult.SuccessResult;
	}
}
