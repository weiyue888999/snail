package com.github.snail.common;


/**
 * @author 		：weiguangyue
 * 常量定义
 */
public class Constants {
	
	public static final String MDC_KEY_INSTANCE_ID = "captcha_instanceId";
	
	public static final String MDC_KEY_SESSION_ID = "captcha_sessionId";
	//TODO 待开发完善,共享CommonResourceLoader
	public static final String COMMON_RESOURCE_LOADER_SERVLET_CONTEXT_KEY = "com.github.snail.resource.CaptchaInnerResourceLoader_commonResourceLoader";
	/**
	 * 嵌入式
	 */
	public static final String CAPTCHA_STYLE_EMBEDDED = "embedded";
	/**
	 * 触发式
	 */
	public static final String CAPTCHA_STYLE_TRIGGER = "trigger";
	/**
	 * 弹出式
	 */
	public static final String CAPTCHA_STYLE_POPUP = "popup";
	
	public static final String CAPTCHA_PAGE_RENDER_PREFIX = "com.github.snail.page_render_";
	
	public static final String CAPTCHA_INNER_IMAGE_REGISTRY_RESOURCE_DIR = "META-INF/com/github/snail/resources/imageRepository/";
	
	public static final String CAPTCHA_INNER_COMMON_RESOURCE = "META-INF/com/github/snail/resources/commons/";
	
	public static final String CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA = "META-INF/com/github/snail/resources/snail/";

	public static final String CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA_POPUP = "META-INF/com/github/snail/resources/snail_popup/";
	
	public static final String CAPTCHA_INNER_RESOURCE_HTML_FILE_NAME = "snail_captcha.html";
	
	public static final String CAPTCHA_INNER_RESOURCE_CSS_FILE_NAME = "snail_captcha.css";
	
	public static final String CAPTCHA_INNER_RESOURCE_SNAIL_JQUERY_FILE_NAME = "snail-jquery-1.11.1.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_SNAIL_JQUERY_ENCRYPTED__FILE_NAME = "snail-jquery-1.11.1-min.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_SNAIL_JQUERY_FILE_NAME = CAPTCHA_INNER_COMMON_RESOURCE + "js/snail-jquery-1.11.1.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_SNAIL_JQUERY_ENCRYPTED_FILE_NAME = CAPTCHA_INNER_COMMON_RESOURCE + "/js/snail-jquery-1.11.1-min.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_FILE_NAME = "snail_captcha.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_FILE_NAME = "snail_captcha_config.js";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_REQUEST_PATH = "CAPTCHA_REQUEST_PATH";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_RTK = "CAPTCHA_RTK";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_INSTANCEID = "CAPTCHA_INSTANCEID";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_STYLE = "CAPTCHA_STYLE";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_STATE = "CAPTCHA_STATE";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_HEIGHT = "CAPTCHA_IMAGE_HEIGHT";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_WIDTH = "CAPTCHA_IMAGE_WIDTH";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_SLIDER_BAR_HEIGHT = "CAPTCHA_SLIDER_BAR_HEIGHT";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_SLIDER_BAR_WIDTH = "CAPTCHA_SLIDER_BAR_WIDTH";
	
	public static final String CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_PANEL_CSS_STYLE_TOP = "CAPTCHA_IMAGE_PANEL_CSS_STYLE_TOP";
	
	public static final String CAPTCHA_INNER_RESOURCE_IMAGE_NAME = "sprite.png";

	public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	
	public static final String CONTENT_TYPE_HTML = "text/plain;charset=utf-8";
	
	public static final String CONTENT_TYPE_CSS = "text/css;charset=utf-8";
	
	public static final String CONTENT_TYPE_PNG = "image/png";
	
	public static final String CONTENT_TYPE_JAVASCRIPT = "application/javascript;charset=utf-8";
	
	public static final String CHARACTER_ENCODING_UTF_8 = "UTF-8";
	
	public static final String HTTP_REQUEST_METHOD_POST = "POST";
	
	public static final String HTTP_REQUEST_METHOD_GET = "GET";
	/**
	 * 没有发现资源
	 */
	public static final int STATUS_CODE_404 = 404;
	/**
	 * 内部错误
	 */
	public static final int STATUS_CODE_500 = 500;
	/**
	 * Method not allowed(方法不被允许)
	 */
	public static final int STATUS_CODE_405 = 405;
	/**
	 * 验证结果存session
	 */
	public static final String VERIFY_RESULT_SESSION_KEY_FORMAT = "CAPTCHA_VERIFY_RESULT_SESSION_KEY_%s";
	
	public static final String RENDER_JSON_KEY_STATUS = "status";
	
	public static final String RENDER_JSON_VALUE_SUCCESS = "success";
	
	public static final String RENDER_JSON_VALUE_FAIL = "fail";
	
	public static final String RENDER_JSON_KEY_MSG = "msg";
	
	public static final String RENDER_JSON_KEY_VERIFE_STATUS = "vs";
	
	//国际化-----------------------------------------------------------start
	
	public static final String LANGUAGE_LOCAL_RESOURCE_BUNDLE_KEY = "com.github.snail.core.CaptchaController.";
	
	public static final String LANGUAGE_LOCAL_SESSION_KEY = "com.github.snail.core.CaptchaController.LANGUAGE_LOCAL_SESSION_KEY";
	
	public static final String LANGUAGE_LOCAL_SESSION_KEY_VALUE_DEFAULT = "zh_CN";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_TIPS_FAIL = "snail.verify.tips.fail";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_TIPS_SUCCESS = "snail.verify.tips.succsss";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_TAG_BAR = "snail.verify.tag.bar";
	
	public static final String KEY_I18N_CAPTCHA_POPUP_TITLE = "snail.verify.popup.title";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_FAIL = "snail.verify.server.tips.fail";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_SUCESS = "snail.verify.server.tips.success";
	
	public static final String KEY_I18N_CAPTCHA_VERIFY_SERVER_TIPS_INNER_ERROR = "snail.verify.server.tips.innerError";
	
	//国际化-----------------------------------------------------------end
}
