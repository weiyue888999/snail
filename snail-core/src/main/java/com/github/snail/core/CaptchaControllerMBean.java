package com.github.snail.core;

import java.util.Set;

/**
 * @author 		：weiguangyue
 */
public interface CaptchaControllerMBean {

	boolean isProcessRequestOrdered();

	void setProcessRequestOrdered(boolean processRequestOrdered);

	boolean isMatchRequestType();

	void setMatchRequestType(boolean matchRequestType);

	boolean isRenderEncryptedJavascript();

	void setRenderEncryptedJavascript(boolean renderEncryptedJavascript);

	boolean isEncryptRequestParam();

	void setEncryptRequestParam(boolean encryptRequestParam);
	
	boolean isKeepVerifyStatusIfVerifySuccess();

	void setKeepVerifyStatusIfVerifySuccess(boolean keepVerifyStatusIfVerifySuccess);
	
	int getImageHeight();

	void setImageHeight(int imageHeight);
	
	int getImageWidth();
	
	void setImageWidth(int imageWeight);

	void setAllowSlideRollback(boolean allowSlideRollback);

	boolean isAllowSlideRollback();

	void setAllowMaxSlideTracePositionCount(int allowMaxSlideTracePositionCount);

	int getAllowMaxSlideTracePositionCount();

	void setAllowMinSlideTracePositionCount(int allowMinSlideTracePositionCount);

	int getAllowMinSlideTracePositionCount();

	void setAllowMaxSlideTimeMillisecond(int allowMaxSlideTimeMillisecond);

	int getAllowMaxSlideTimeMillisecond();

	void setAllowMinSlideTimeMillisecond(int allowMinSlideTimeMillisecond);

	int getAllowMinSlideTimeMillisecond();

	void setAllowMinMissMathDistance(int allowMinMissMathDistance);

	int getAllowMinMissMathDistance();

	Set<String> getClientUserAgents();

	boolean removeClientUserAgent(String clientUserAgent);

	boolean addClientUserAgent(String clientUserAgent);

	boolean isMatchClientUserAgent();

	void setMatchClientUserAgent(boolean matchClientUserAgent);

	long getAllowRefreshMinInterval();

	void setAllowRefreshMinInterval(long allowRefreshMinInterval);
	
	String getFilterUrlPatternMapping();
	
	String getCaptchaRequestPath();
	
	String getCaptchaPageRender();
	
	String getCaptchaJavascriptConfigCodeTemplate();
	
	String getFormattedCaptchaJavascriptConfigCodeTemplate();
	
	String dump();

	int getSliderBarHeight();

	void setSliderBarHeight(int sliderBarWidth);

	int getSliderBarWidth();

	void setSliderBarWidth(int sliderBarWidth);

	int getImageBlockSize();

	void setImageBlockSize(int imageBlockSize);
	
	String getImageSelectorName();

	void setImageSelectorName(String imageSelectorName);

	void setImagePanelCssStyleTop(int imagePanelCssStyleTop);

	int getImagePanelCssStyleTop();

	void setImageBlockCircularRadiusSize(int imageBlockCircularRadiusSize);

	int getImageBlockCircularRadiusSize();

	boolean isMatchRtk();

	void setMatchRtk(boolean matchRtk);
	
	boolean isRenderCompanyLogo();

	void setRenderCompanyLogo(boolean renderCompanyLogo);

	String getCaptchaStyle();

	void setCaptchaStyle(String captchaStyle);

	String getInstanceId();

	void refresh();

	String getCaptchaRenderContainerHtmlSegment();
}
