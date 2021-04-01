package com.github.snail.core;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import com.github.snail.base.BaseLifecycleMBean;
import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.resource.CacheableCaptchaInnerResourceLoader;
import com.github.snail.resource.CaptchaInnerResourceLoader;

/**
 * @author ：weiguangyue
 * 
 * 动态控制配置,开发阶段好测试,要不然改一个配置就要重启一下，你说烦不烦啊！！！
 * 
 * toString()方法最好别乱动哦，要按格式来一行一行排列的哦,这个其实就是让人家dump出来配置,这样就可以拷贝到配置文件了 :)
 */
public class CaptchaController extends BaseLifecycleMBean implements CaptchaControllerMBean {
	
	private static final Log log = LogFactory.getLog(CaptchaController.class);
	
	private final String instanceId;
	
	private final ServletContext servletContext;
	
	/**
	 * 是否顺序处理请求,一般开发模式可使用此选项便于观顺序察请求,生产环境下设置此选项会影响性能
	 */
	private volatile boolean processRequestOrdered = false;
	/**
	 * 是否严格匹配请求类型,防止恶意用户使用浏览器直接访问RefreshFilter和VerifyFilter
	 */
	private volatile boolean matchRequestType = true;
	
	//-------滑动验证配置--------------------------------------------------------------------------------start
	private volatile boolean matchRtk = true;
	/**
	 * 保持以及验证成功的状态,当清除会话才会重新起效果,暂未开发出来
	 * 当验证通过之后，如果保存这个状态，这样，用户就不用再进行滑动验证了
	 */
	private volatile boolean keepVerifyStatusIfVerifySuccess = true;
	/**
	 * 允许最小的匹配差距 2个像素
	 */
	private volatile int allowMinMissMathDistance = 2;
	/**
	 * 允许最小的滑动时间,10毫秒
	 */
	private volatile int allowMinSlideTimeMillisecond = 10;
	/**
	 * 允许最大的滑动时间,8000毫秒,再多我怀疑是手残了!!!
	 */
	private volatile int allowMaxSlideTimeMillisecond = 8000;
	/**
	 * 允许最小滑动轨迹坐标个数
	 */
	private volatile int allowMinSlideTracePositionCount = 10;
	/**
	 * 允许最大滑动轨迹坐标个数
	 */
	private volatile int allowMaxSlideTracePositionCount = 1000;
	/**
	 * 允许的最小刷新间隔,单位毫秒
	 */
	private volatile long allowRefreshMinInterval = 2000;
	/**
	 * 是否允许回头
	 */
	private volatile boolean allowSlideRollback = true;
	/**
	 * 启用严格匹配用户代理,防止最简单的爬虫
	 */
	private volatile boolean matchClientUserAgent = false;
	/**
	 * 浏览器客户端用户代理
	 */
	private Set<String> clientUserAgents = new HashSet<String>(//
		Arrays.asList(//
				"Firefox",//
				"Seamonkey",//
				"Chrome",//
				"Chromium",//
				"Safari",//
				"OPR",//
				"Opera",//
				"MSIE"//
		)//
	);//
	//-------滑动验证配置--------------------------------------------------------------------------------end
	
	//-------上下文相关--------------------------------------------------------------------------------start
	/**
	 * filter映射的路径
	 */
	private final String filterUrlPatternMapping;

	//-------上下文相关--------------------------------------------------------------------------------end

	//-------前端相关--------------------------------------------------------------------------------start
	/**
	 * 验证码风格
	 */
	private volatile String captchaStyle;
	/**
	 * 图片选择器
	 */
	private volatile String imageSelectorName = "RoundRobinImageSelector";
	/**
	 * 验证码javascript代码中的配置代码模板
	 */
	private volatile String captchaJavascriptConfigCodeTemplate = "";
	/**
	 * 渲染验证码容器的html代码片段
	 */
	private volatile String captchaRenderContainerHtmlSegment = "";
	/**
	 * 图片高度,单位像素
	 */
	private volatile int imageHeight = 200;
	/**
	 * 图片宽度,单位像素
	 */
	private volatile int imageWidth = 338;
	/**
	 * 背景图片pannel的css样式的top值
	 */
	private volatile int imagePanelCssStyleTop = 58;
	/**
	 * 是否渲染加密的javascript//TODO
	 */
	private volatile boolean renderEncryptedJavascript = true;
	/**
	 * 是否加密请求参数,一般生产环境加密请求参数,开发此组件或调试时不加密请求参数//TODO
	 */
	private volatile boolean encryptRequestParam = false;
	/**
	 * 滑块高度,单位像素
	 */
	private volatile int sliderBarHeight = 38;
	/**
	 * 滑块高度,单位像素
	 */
	private volatile int sliderBarWidth = 48;
	/**
	 * 图片上被挖走的块的高和宽,在这里高和宽必须一样,单位像素
	 */
	private volatile int imageBlockSize = 60;
	/**
	 * 图片上被挖走的块的圆形的半径
	 */
	private volatile int imageBlockCircularRadiusSize = 12;
	
	//是否在备选图片上渲染公司logo
	private volatile boolean renderCompanyLogo = false;
	//-------前端相关--------------------------------------------------------------------------------end
	
	//不同风格加载不同的目录
	private CaptchaInnerResourceLoader captchaInnerResourceLoader;
	
	public CaptchaController(String instanceId,String captchaStyle,String mapping, FilterConfig filterConfig) {
		
		this.instanceId = instanceId;
		this.captchaStyle = captchaStyle;
		this.filterUrlPatternMapping = mapping;
		this.servletContext = filterConfig.getServletContext();
		
		this.init(filterConfig);
	}
	
	private void init(FilterConfig filterConfig) {
		
		{
			String val = filterConfig.getInitParameter("keepVerifyStatusIfVerifySuccess");
			this.keepVerifyStatusIfVerifySuccess = val != null ? Boolean.parseBoolean(val) : this.keepVerifyStatusIfVerifySuccess;
		}
		{
			String val = filterConfig.getInitParameter("processRequestOrdered");
			this.processRequestOrdered = val != null ? Boolean.parseBoolean(val) : this.processRequestOrdered;
		}
		
		{
			String val = filterConfig.getInitParameter("matchRequestType");
			this.matchRequestType = val != null ? Boolean.parseBoolean(val) : this.matchRequestType;
		}
		
		{
			String val = filterConfig.getInitParameter("matchRtk");
			this.matchRtk = val != null ? Boolean.parseBoolean(val) : this.matchRtk;
		}
		
		{
			String val = filterConfig.getInitParameter("renderEncryptedJavascript");
			this.renderEncryptedJavascript = val != null ? Boolean.parseBoolean(val) : this.renderEncryptedJavascript;
		}
		
		{
			String val = filterConfig.getInitParameter("encryptRequestParam");
			this.encryptRequestParam = val != null ? Boolean.parseBoolean(val) : this.encryptRequestParam;
		}
		
		{
			String val = filterConfig.getInitParameter("allowMinMissMathDistance");
			this.allowMinMissMathDistance = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "allowMinMissMathDistance") : this.allowMinMissMathDistance;
		}
		
		{
			String val = filterConfig.getInitParameter("allowMinSlideTracePositionCount");
			this.allowMinSlideTracePositionCount = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "allowMinSlideTracePositionCount") : this.allowMinSlideTracePositionCount;
		}
		
		{
			String val = filterConfig.getInitParameter("allowMaxSlideTracePositionCount");
			this.allowMaxSlideTracePositionCount = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "allowMaxSlideTracePositionCount") : this.allowMaxSlideTracePositionCount;
		}
		
		{
			String val = filterConfig.getInitParameter("matchClientUserAgent");
			this.matchClientUserAgent = val != null ? Boolean.parseBoolean(val) : this.matchClientUserAgent;
		}
		
		{
			String val = filterConfig.getInitParameter("imageHeight");
			this.imageHeight = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "imageHeight") : this.imageHeight;
		}
		
		{
			String val = filterConfig.getInitParameter("imageWidth");
			this.imageWidth = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "imageWidth") : this.imageWidth; 
		}
		
		{
			String val = filterConfig.getInitParameter("imageBlockSize");
			this.imageBlockSize = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "imageBlockSize") : this.imageBlockSize; 
		}
		
		{
			String val = filterConfig.getInitParameter("imageBlockCircularRadiusSize");
			this.imageBlockCircularRadiusSize = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "imageBlockCircularRadiusSize") : this.imageBlockCircularRadiusSize;
		}
		
		{
			String val = filterConfig.getInitParameter("imagePanelCssStyleTop");
			this.imagePanelCssStyleTop = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "imagePanelCssStyleTop") : this.imagePanelCssStyleTop; 
		}
		
		{
			String val = filterConfig.getInitParameter("allowMinSlideTimeMillisecond");
			this.allowMinSlideTimeMillisecond = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "allowMinSlideTimeMillisecond") : this.allowMinSlideTimeMillisecond;
		}
		
		{
			String val = filterConfig.getInitParameter("allowMaxSlideTimeMillisecond");
			this.allowMaxSlideTimeMillisecond = Utils.isNotEmpty(val) ? Utils.parsePositiveInt(val, "allowMaxSlideTimeMillisecond") : this.allowMaxSlideTimeMillisecond;
		}
		
		{
			String val = filterConfig.getInitParameter("allowSlideRollback");
			this.allowSlideRollback = val != null ? Boolean.parseBoolean(val) : this.allowSlideRollback;
		}
		
		{
			String val = filterConfig.getInitParameter("allowSlideRollback");
			this.allowSlideRollback = val != null ? Boolean.parseBoolean(val) : this.allowSlideRollback;
		}
		
		{
			String val = filterConfig.getInitParameter("renderCompanyLogo");
			this.renderCompanyLogo = val != null ? Boolean.parseBoolean(val) : this.renderCompanyLogo;
		}
		
		{
			String val = filterConfig.getInitParameter("clientUserAgents");
			if(val != null) {
				List<String> vals = Arrays.<String>asList(Utils.split(val, ";"));
				for(String str : vals) {
					addClientUserAgent(str);					
				}
			}
		}
	}
	
	@Override
	protected void doStart() {
		super.doStart();
		
		this.refresh();
		
		this.servletContext.setAttribute(getAttributeName(),this);
		
		log.info("CaptchaController[instanceId="+ this.instanceId +",captchaStyle="+ this.captchaStyle +",filterUrlPatternMapping="+ this.filterUrlPatternMapping +"] startup complate");
	}

	@Override
	protected void doStop() {
		super.doStop();
		
		log.info("CaptchaController[instanceId="+ this.instanceId +",captchaStyle="+ this.captchaStyle +",filterUrlPatternMapping="+ this.filterUrlPatternMapping +"] stop complate");
		
		this.servletContext.removeAttribute(getAttributeName());
	}

	private String getAttributeName() {
		return CaptchaController.class.getName()+"_"+this.instanceId;
	}

	private void refreshRender() {
		//embedded(嵌入式)和trigger(触发式)采用相同的js和html
		if(Constants.CAPTCHA_STYLE_EMBEDDED.equals(this.captchaStyle) || Constants.CAPTCHA_STYLE_TRIGGER.equals(this.captchaStyle)){
			//this.captchaInnerResourceLoader = new DefaultCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_RESOURCE_DIR_ZFCAPTCHA);
			this.captchaInnerResourceLoader = new CacheableCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA);
		}else if(Constants.CAPTCHA_STYLE_POPUP.equals(this.captchaStyle)){
			//弹出式采用不同的html和js
			//TODO
			//this.captchaInnerResourceLoader = new DefaultCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA_POPUP);
			this.captchaInnerResourceLoader = new CacheableCaptchaInnerResourceLoader(Constants.CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA_POPUP);
		}else{
			throw new IllegalStateException("undefined captchaStyole : "+ this.captchaStyle);
		}
		try {
			String html = new String(this.captchaInnerResourceLoader.loadHtml(Constants.CAPTCHA_INNER_RESOURCE_HTML_FILE_NAME),Constants.CHARACTER_ENCODING_UTF_8);
			this.captchaRenderContainerHtmlSegment = html;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("load html resource error , cause: "+e.getMessage(),e);
		}
		try {
			String template = new String(this.captchaInnerResourceLoader.loadJavascript(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_FILE_NAME),Constants.CHARACTER_ENCODING_UTF_8);
			this.captchaJavascriptConfigCodeTemplate = template;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("load javascript resource error , cause: "+e.getMessage(),e);
		}

		this.servletContext.setAttribute(getPageRenderAttributeName(),getCaptchaPageRender());
	}
	
	private String getPageRenderAttributeName() {
		return Constants.CAPTCHA_PAGE_RENDER_PREFIX+this.instanceId;
	}

	@Override
	public boolean isProcessRequestOrdered() {
		return processRequestOrdered;
	}

	@Override
	public void setProcessRequestOrdered(boolean processRequestOrdered) {
		this.processRequestOrdered = processRequestOrdered;
	}

	@Override
	public boolean isMatchRequestType() {
		return matchRequestType;
	}

	@Override
	public void setMatchRequestType(boolean matchRequestType) {
		this.matchRequestType = matchRequestType;
	}

	@Override
	public boolean isRenderEncryptedJavascript() {
		return renderEncryptedJavascript;
	}

	@Override
	public void setRenderEncryptedJavascript(boolean renderEncryptedJavascript) {
		this.renderEncryptedJavascript = renderEncryptedJavascript;
	}

	@Override
	public boolean isEncryptRequestParam() {
		return encryptRequestParam;
	}

	@Override
	public void setEncryptRequestParam(boolean encryptRequestParam) {
		this.encryptRequestParam = encryptRequestParam;
	}

	@Override
	public boolean isKeepVerifyStatusIfVerifySuccess() {
		return keepVerifyStatusIfVerifySuccess;
	}

	@Override
	public void setKeepVerifyStatusIfVerifySuccess(boolean keepVerifyStatusIfVerifySuccess) {
		this.keepVerifyStatusIfVerifySuccess = keepVerifyStatusIfVerifySuccess;
	}

	@Override
	public int getImageHeight() {
		return imageHeight;
	}

	@Override
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	@Override
	public int getImageWidth() {
		return imageWidth;
	}

	@Override
	public void setImageWidth(int imageHeight) {
		this.imageWidth = imageHeight;
	}

	@Override
	public int getAllowMinMissMathDistance() {
		return allowMinMissMathDistance;
	}

	@Override
	public void setAllowMinMissMathDistance(int allowMinMissMathDistance) {
		this.allowMinMissMathDistance = allowMinMissMathDistance;
	}

	@Override
	public int getAllowMinSlideTimeMillisecond() {
		return allowMinSlideTimeMillisecond;
	}

	@Override
	public void setAllowMinSlideTimeMillisecond(int allowMinSlideTimeMillisecond) {
		this.allowMinSlideTimeMillisecond = allowMinSlideTimeMillisecond;
	}

	@Override
	public int getAllowMaxSlideTimeMillisecond() {
		return allowMaxSlideTimeMillisecond;
	}

	@Override
	public void setAllowMaxSlideTimeMillisecond(int allowMaxSlideTimeMillisecond) {
		this.allowMaxSlideTimeMillisecond = allowMaxSlideTimeMillisecond;
	}

	@Override
	public int getAllowMinSlideTracePositionCount() {
		return allowMinSlideTracePositionCount;
	}

	@Override
	public void setAllowMinSlideTracePositionCount(int allowMinSlideTracePositionCount) {
		this.allowMinSlideTracePositionCount = allowMinSlideTracePositionCount;
	}

	@Override
	public int getAllowMaxSlideTracePositionCount() {
		return allowMaxSlideTracePositionCount;
	}

	@Override
	public void setAllowMaxSlideTracePositionCount(int allowMaxSlideTracePositionCount) {
		this.allowMaxSlideTracePositionCount = allowMaxSlideTracePositionCount;
	}

	@Override
	public boolean isAllowSlideRollback() {
		return allowSlideRollback;
	}

	@Override
	public void setAllowSlideRollback(boolean allowSlideRollback) {
		this.allowSlideRollback = allowSlideRollback;
	}
	
	@Override
	public boolean isMatchClientUserAgent() {
		return matchClientUserAgent;
	}

	@Override
	public void setMatchClientUserAgent(boolean matchClientUserAgent) {
		this.matchClientUserAgent = matchClientUserAgent;
	}

	@Override
	public Set<String> getClientUserAgents() {
		return Collections.unmodifiableSet(clientUserAgents);
	}
	
	@Override
	public boolean removeClientUserAgent(String clientUserAgent) {
		return this.clientUserAgents.remove(clientUserAgent);
	}

	@Override
	public boolean addClientUserAgent(String clientUserAgent) {
		return this.clientUserAgents.add(clientUserAgent);
	}
	
	@Override
	public long getAllowRefreshMinInterval() {
		return allowRefreshMinInterval;
	}

	@Override
	public void setAllowRefreshMinInterval(long allowRefreshMinInterval) {
		this.allowRefreshMinInterval = allowRefreshMinInterval;
	}

	@Override
	public String getFilterUrlPatternMapping() {
		return filterUrlPatternMapping;
	}
	
	@Override
	public String getFormattedCaptchaJavascriptConfigCodeTemplate() {
		String formated = 
				this.captchaJavascriptConfigCodeTemplate//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_REQUEST_PATH, this.getCaptchaRequestPath())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_HEIGHT, ""+this.getImageHeight())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_WIDTH, ""+this.getImageWidth())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_SLIDER_BAR_HEIGHT, ""+this.getSliderBarHeight())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_SLIDER_BAR_WIDTH, ""+this.getSliderBarWidth())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_STYLE, this.getCaptchaStyle())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_INSTANCEID, this.getInstanceId())//
				.replace(Constants.CAPTCHA_INNER_RESOURCE_JAVASCRIPT_CONFIG_CODE_TAG_CAPTCHA_IMAGE_PANEL_CSS_STYLE_TOP, ""+this.getImagePanelCssStyleTop());//
		return formated;
	}
	
	@Override
	public String getCaptchaJavascriptConfigCodeTemplate() {
		return this.captchaJavascriptConfigCodeTemplate;
	}

	@Override
	public String getCaptchaPageRender() {
		String jquery = this.getCaptchaRenderJqueryHtmlSegment();
		String html = this.getCaptchaRenderContainerHtmlSegment();
		String js = this.getCaptchaRenderJavascriptHtmlSegment();
		String css = this.getCaptchaRenderCssHtmlSegment();
		List<String> list = Arrays.asList(jquery,html,css,js);
		String result = Utils.join(list,"\n");
		return result;
	}

	@Override
	public String getCaptchaRenderContainerHtmlSegment() {
		return captchaRenderContainerHtmlSegment;
	}
	
	private String getCaptchaRenderJqueryHtmlSegment() {
		if(this.isRenderEncryptedJavascript()) {
			String format = "<script type='text/javascript' src='%s?type=resource&instanceId=%s&name=snail-jquery-1.11.1-min.js'></script>";
			return String.format(format, this.getCaptchaRequestPath(),getInstanceId());
		}else {
			String format = "<script type='text/javascript' src='%s?type=resource&instanceId=%s&name=snail-jquery-1.11.1.js'></script>";
			return String.format(format, this.getCaptchaRequestPath(),getInstanceId());
		}
	}

	private String getCaptchaRenderJavascriptHtmlSegment() {
		String format = "<script type='text/javascript' src='%s?type=resource&instanceId=%s&name=snail_captcha.js'></script>";
		return String.format(format, this.getCaptchaRequestPath(),getInstanceId());
	}
	
	private String getCaptchaRenderCssHtmlSegment() {
		String format = "<link rel='stylesheet' href='%s?type=resource&instanceId=%s&name=snail_captcha.css' />";
		return String.format(format, this.getCaptchaRequestPath(),getInstanceId());
	}
	
	@Override
	public String getCaptchaRequestPath() {
		return this.servletContext.getContextPath() + this.filterUrlPatternMapping;
	}
	
	@Override
	public int getSliderBarHeight() {
		return sliderBarHeight;
	}

	@Override
	public void setSliderBarHeight(int sliderBarHeight) {
		this.sliderBarHeight = sliderBarHeight;
	}

	@Override
	public int getSliderBarWidth() {
		return sliderBarWidth;
	}

	@Override
	public void setSliderBarWidth(int sliderBarWidth) {
		this.sliderBarWidth = sliderBarWidth;
	}

	@Override
	public int getImageBlockSize() {
		return imageBlockSize;
	}

	@Override
	public void setImageBlockSize(int imageBlockSize) {
		this.imageBlockSize = imageBlockSize;
	}
	
	@Override
	public String getImageSelectorName() {
		return imageSelectorName;
	}

	@Override
	public void setImageSelectorName(String imageSelectorName) {
		this.imageSelectorName = imageSelectorName;
	}

	@Override
	public int getImageBlockCircularRadiusSize() {
		return imageBlockCircularRadiusSize;
	}

	@Override
	public void setImageBlockCircularRadiusSize(int imageBlockCircularRadiusSize) {
		this.imageBlockCircularRadiusSize = imageBlockCircularRadiusSize;
	}
	
	@Override
	public int getImagePanelCssStyleTop() {
		return imagePanelCssStyleTop;
	}

	@Override
	public void setImagePanelCssStyleTop(int imagePanelCssStyleTop) {
		this.imagePanelCssStyleTop = imagePanelCssStyleTop;
	}

	@Override
	public boolean isMatchRtk() {
		return matchRtk;
	}

	@Override
	public void setMatchRtk(boolean matchRtk) {
		this.matchRtk = matchRtk;
	}
	
	@Override
	public boolean isRenderCompanyLogo() {
		return renderCompanyLogo;
	}

	@Override
	public void setRenderCompanyLogo(boolean renderCompanyLogo) {
		this.renderCompanyLogo = renderCompanyLogo;
	}

	@Override
	public String getCaptchaStyle() {
		return captchaStyle;
	}

	@Override
	public void setCaptchaStyle(String captchaStyle) {
		if(Utils.isEmpty(captchaStyle)) {
			throw new IllegalArgumentException("empty str");
		}
		Set<String> set = new HashSet<String>();
		set.add(Constants.CAPTCHA_STYLE_POPUP);
		set.add(Constants.CAPTCHA_STYLE_EMBEDDED);
		set.add(Constants.CAPTCHA_STYLE_TRIGGER);
		
		if(!set.contains(captchaStyle)) {
			throw new IllegalArgumentException("param must be [embedded,trigger,popup]");
		}
		
		if(Constants.CAPTCHA_STYLE_POPUP.equals(this.captchaStyle)) {
			throw new IllegalStateException("can't change captchaStyel[popup] to other style!!!");
		}
		if((Constants.CAPTCHA_STYLE_EMBEDDED.equals(this.captchaStyle) || Constants.CAPTCHA_STYLE_TRIGGER.equals(this.captchaStyle)) && Constants.CAPTCHA_STYLE_POPUP.equals(captchaStyle)) {
			throw new IllegalStateException("can't change captchaStyel[embedded,trigger] to popup style!!!");
		}
		this.captchaStyle = captchaStyle;
	}

	@Override
	public String getInstanceId() {
		return instanceId;
	}
	
	public CaptchaInnerResourceLoader getCaptchaInnerResourceLoader() {
		return captchaInnerResourceLoader;
	}
	
	@Override
	public void refresh() {
		this.refreshRender();
		log.info("CaptchaController[instanceId="+ this.instanceId +",captchaStyle="+ this.captchaStyle +",filterUrlPatternMapping="+ this.filterUrlPatternMapping +"] refresh complate");
		log.info("PagerRender["+ getPageRenderAttributeName() +"] refresh complate");
	}

	@Override
	public String dump() {
		return this.toString();
	}

	@Override
	public String toString() {
		return "CaptchaController \n"
			+ "[\n"
				+ " instanceId=" + instanceId + ",\n"
				+ " imageHeight=" + imageHeight + ",\n"
				+ " imageWidth=" + imageWidth + ",\n"
				+ " captchaStyle=" + captchaStyle + ",\n"
				+ " imagePanelCssStyleTop=" + imagePanelCssStyleTop + ",\n"
				+ " imageBlockSize=" + imageBlockSize + ",\n"
				+ " imageBlockCircularRadiusSize=" + imageBlockCircularRadiusSize + ",\n"
				+ " processRequestOrdered=" + processRequestOrdered	+ ",\n"
				+ " matchRequestType=" + matchRequestType + ",\n"
				+ " renderEncryptedJavascript=" + renderEncryptedJavascript	+ ",\n"
				+ " renderCompanyLogo=" + renderCompanyLogo	+ ",\n"
				+ " encryptRequestParam=" + encryptRequestParam + ",\n"
				+ " keepVerifyStatusIfVerifySuccess="+ keepVerifyStatusIfVerifySuccess + ",\n"
				+ " allowMinMissMathDistance=" + allowMinMissMathDistance + ",\n"
				+ " allowMinSlideTimeMillisecond=" + allowMinSlideTimeMillisecond + ",\n"
				+ " allowMaxSlideTimeMillisecond=" + allowMaxSlideTimeMillisecond + ",\n"
				+ " allowMinSlideTracePositionCount=" + allowMinSlideTracePositionCount	+ ",\n"
				+ " allowMaxSlideTracePositionCount=" + allowMaxSlideTracePositionCount + ",\n"
				+ " allowRefreshMinInterval=" + allowRefreshMinInterval + ",\n"
				+ " allowSlideRollback=" + allowSlideRollback + ",\n"
				+ " matchClientUserAgent=" + matchClientUserAgent + ",\n"
				+ " imageSelectorName=" + imageSelectorName + ",\n"
				+ " clientUserAgents=" + clientUserAgents + ",\n"
				+ " servletContext=" + servletContext + "\n"
				+ " sliderBarHeight=" + sliderBarHeight + "\n"
				+ " sliderBarWidth=" + sliderBarWidth + "\n"
				+ " captchaRenderContainerHtmlSegment=" + captchaRenderContainerHtmlSegment + "\n"
				+ " captchaJavascriptConfigCodeTemplate=" + captchaJavascriptConfigCodeTemplate + "\n"
				+ " filterUrlPatternMapping=" + filterUrlPatternMapping + "\n"
			+ "]";
	}

}
