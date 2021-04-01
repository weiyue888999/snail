package com.github.snail.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.TempFileService;
import com.github.snail.core.Verify;
import com.github.snail.core.VerifyFactory;
import com.github.snail.core.ClientVerifyContext.State;
import com.github.snail.core.impl.DefaultVerifyFactory;
import com.github.snail.core.impl.TimeVerifyFactory;
import com.github.snail.exceptions.VerifyException;
import com.github.snail.i18n.ResourceBundleFactory;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
@author ：weiguangyue
 
 滑动验证码图片刷新Filter
 */
class RefreshFilter extends AbstractCaptchaControllerFilter{

	private static final Log log = LogFactory.getLog(RefreshFilter.class);
	
	private VerifyFactory verifyFactory = null;
	
	public RefreshFilter(CaptchaController captchaController) {
		super(captchaController);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		//can't be null
		TempFileService tempFileService = (TempFileService) filterConfig.getServletContext().getAttribute(TempFileService.class.getName());
		
		this.verifyFactory = new DefaultVerifyFactory(getCaptchaController(),tempFileService);
		
		this.verifyFactory = new TimeVerifyFactory(this.verifyFactory);
		
		this.verifyFactory.start();
	}

	@Override
	public void destroy() {
		this.verifyFactory.stop();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain chain) throws IOException, ServletException{
		
		final ClientVerifyContext verifyContext = ClientVerifyContext.get(request,getCaptchaController());
		if(this.getCaptchaController().isKeepVerifyStatusIfVerifySuccess() && verifyContext.getState() == State.VERIFIED) {
			onFinishVerifiedOnBeforeSomeTime(request,response);
		}else {
			//TODO 防止频繁刷新,在2秒内最多只能让刷新一次
			if(verifyContext.getRefreshCount() > 2 && System.currentTimeMillis() - verifyContext.getLastRefershTime() < 2000) {
				//TODO 此处要同步修改前端相关逻辑
				onNotFinishVerified(verifyContext,request,response);
			}else {
				onNotFinishVerified(verifyContext,request,response);
			}
		}
	}
	
	private void onNotFinishVerified(ClientVerifyContext verifyContext, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		try {
			
			String attributeName = String.format(Constants.VERIFY_RESULT_SESSION_KEY_FORMAT, getCaptchaController().getInstanceId());
			
			verifyContext.setState(State.NOT_VERIFY);
			verifyContext.refreshVerify(null);
			request.getSession().removeAttribute(attributeName);
			
			Verify newVerify = this.verifyFactory.create();

			String srcImageFileId = Utils.getName(newVerify.getSi().toString());
			String markImageFileId = Utils.getName(newVerify.getMi().toString());
			String imtk = newVerify.getImtk();
			long time = newVerify.getCt().getTime();

			verifyContext.refreshVerify(newVerify);

			Map<String, Object> resultMap = new HashMap<String, Object>(7);
			resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_SUCCESS);
			resultMap.put(Constants.RENDER_JSON_KEY_MSG, "");
			resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS,State.NOT_VERIFY.tag());
			resultMap.put("imtk", imtk);
			resultMap.put("t", time);
			resultMap.put("si", srcImageFileId);
			resultMap.put("mi", markImageFileId);

			this.renderJson(request, response, resultMap);

		} catch (VerifyException e) {
			log.error("create verify error,cause :" + e.getMessage(), e);

			Map<String, Object> resultMap = new HashMap<String, Object>(3);
			resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_FAIL);
			resultMap.put(Constants.RENDER_JSON_KEY_MSG,ResourceBundleFactory.get(request.getSession()).getString(Constants.KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_INNER_ERROR));
			resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS, State.NOT_VERIFY.tag());

			this.renderJson(request, response, resultMap);

		} catch (Exception e) {

			log.error("inner erro ,cause :" + e.getMessage(), e);

			Map<String, Object> resultMap = new HashMap<String, Object>(3);
			resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_FAIL);
			resultMap.put(Constants.RENDER_JSON_KEY_MSG,ResourceBundleFactory.get(request.getSession()).getString(Constants.KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_INNER_ERROR));
			resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS, State.NOT_VERIFY.tag());

			this.renderJson(request, response, resultMap);
		}finally {
			verifyContext.increaseRefreshCount();
			verifyContext.refreshLastRefreshTime();
		}
	}

	private void onFinishVerifiedOnBeforeSomeTime(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put(Constants.RENDER_JSON_KEY_STATUS, Constants.RENDER_JSON_VALUE_SUCCESS);
		resultMap.put(Constants.RENDER_JSON_KEY_MSG, "");
		resultMap.put(Constants.RENDER_JSON_KEY_VERIFE_STATUS,State.VERIFIED.tag());
		
		this.renderJson(request, response, resultMap);
	}

	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_POST,Constants.HTTP_REQUEST_METHOD_GET);
	}
}
