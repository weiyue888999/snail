package com.github.snail.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.ClientVerifyContext.State;
import com.github.snail.decoder.TracePositionTraceDecoder;
import com.github.snail.decoder.impl.Base64TracePositionTraceDecoder;
import com.github.snail.i18n.ResourceBundleFactory;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.resource.CacheableCaptchaInnerResourceLoader;
import com.github.snail.resource.CaptchaInnerResourceLoader;

/**
 * @author 		：weiguangyue
 */
class InnerResourceRenderFilter extends AbstractCaptchaControllerFilter{

	private static final Log log = LogFactory.getLog(InnerResourceRenderFilter.class);
	
	private final Set<String> resourceNames = new HashSet<String>(3);
	
	private final Set<String> commonsResourceNames = new HashSet<String>(2);
	
	//private CaptchaInnerResourceLoader commonCaptchaInnerResourceLoader = new DefaultCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_COMMON_RESOURCE);
	private CaptchaInnerResourceLoader commonCaptchaInnerResourceLoader = new CacheableCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_COMMON_RESOURCE);
	
	public InnerResourceRenderFilter(CaptchaController captchaController) {
		super(captchaController);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.resourceNames.add(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_FILE_NAME);
		this.resourceNames.add(Constants.CAPTCHA_INNER_RESOURCE_IMAGE_NAME);
		this.resourceNames.add(Constants.CAPTCHA_INNER_RESOURCE_CSS_FILE_NAME);
		
		this.commonsResourceNames.add(Constants.CAPTCHA_INNER_RESOURCE_SNAIL_JQUERY_FILE_NAME);
		this.commonsResourceNames.add(Constants.CAPTCHA_INNER_RESOURCE_SNAIL_JQUERY_ENCRYPTED__FILE_NAME);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain chain)throws IOException, ServletException {
		String name = request.getParameter("name");
		if(Utils.isEmpty(name)) {
			log.warn("download inner resource null !!! mybe someone want study me");
			response.sendError(Constants.STATUS_CODE_404);
		}else {
			if(this.resourceNames.contains(name)) {
				if(name.endsWith(".js")) {
					
					ClientVerifyContext verifyContext = ClientVerifyContext.get(request,getCaptchaController());
					
					if(!this.getCaptchaController().isKeepVerifyStatusIfVerifySuccess()) {
						State oldStatus = verifyContext.getState();
						if(oldStatus == State.VERIFIED) {
							log.info("reset status to not_verify");
						}
						verifyContext.setState(State.NOT_VERIFY);
					}
					//加载配置代码
					String javascriptConfigCodeTemplate = this.getCaptchaController().getFormattedCaptchaJavascriptConfigCodeTemplate();
					String afterReplaceCode = javascriptConfigCodeTemplate.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_RTK, verifyContext.getRtk());
					String afterReplaceStateCode = afterReplaceCode.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_STATE,verifyContext.getState().tag());
					
					ResourceBundle boundle = ResourceBundleFactory.get(request.getSession());
					String msg1 = boundle.getString(Constants.KEY_I18N_CAPTCHA_VERIFY_TIPS_FAIL);
					String msg2 = boundle.getString(Constants.KEY_I18N_CAPTCHA_VERIFY_TIPS_SUCCESS);
					String msg3 = boundle.getString(Constants.KEY_I18N_CAPTCHA_VERIFY_TAG_BAR);
					String msg4 = boundle.getString(Constants.KEY_I18N_CAPTCHA_POPUP_TITLE);
					
					String afterReplaceI18nCode = afterReplaceStateCode
							.replace("KEY_I18N_CAPTCHA_VERIFY_TIPS_FAIL",msg1)
							.replace("KEY_I18N_CAPTCHA_VERIFY_TIPS_SUCCESS", msg2)
							.replace("KEY_I18N_CAPTCHA_VERIFY_TAG_BAR", msg3)
							.replace("KEY_I18N_CAPTCHA_POPUP_TITLE", msg4);
					
					//加载组件渲染代码
					byte[] byteArray = this.getCaptchaController().getCaptchaInnerResourceLoader().loadJavascript(name);
					
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append(afterReplaceI18nCode);
					stringBuilder.append(new String(byteArray,"UTF-8"));
					
					this.renderFileByte(request, response, Constants.CONTENT_TYPE_JAVASCRIPT, stringBuilder.toString().getBytes("UTF-8"), name);
				}else if(name.endsWith(".css")){
					byte[] byteArray = this.getCaptchaController().getCaptchaInnerResourceLoader().loadCss(name);
					this.renderFileByte(request, response, Constants.CONTENT_TYPE_CSS, byteArray, name);
				}else if(name.endsWith(".png")) {
					byte[] byteArray = this.getCaptchaController().getCaptchaInnerResourceLoader().loadImage(name);
					this.renderFileByte(request, response, Constants.CONTENT_TYPE_PNG, byteArray, name);
				}else {
					log.warn("download inner resource["+ name +"] not js,css or png !!! mybe someone want study me");
					response.setStatus(Constants.STATUS_CODE_404);
				}
			}else if(commonsResourceNames.contains(name)) {
				if(name.endsWith(".js")) {
					
					List<byte[]> datas = new ArrayList<byte[]>(2);
					
					TracePositionTraceDecoder decoder = new Base64TracePositionTraceDecoder();
					datas.add(decoder.javascriptEncodeCode().getBytes("UTF-8"));
					
					byte[] commonResourceByteArray = commonCaptchaInnerResourceLoader.loadJavascript(name);
					datas.add(commonResourceByteArray);
					
					this.renderFileByte(request, response, Constants.CONTENT_TYPE_JAVASCRIPT, datas, name);
				}else {
					log.warn("download inner resource["+ name +"] not exist !!! mybe someone want study me");
					response.setStatus(Constants.STATUS_CODE_404);
				}
			}else {
				log.warn("download inner resource["+ name +"] not exist !!! mybe someone want study me");
				response.setStatus(Constants.STATUS_CODE_404);
			}
		}
	}
	
	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_GET);
	}
}
