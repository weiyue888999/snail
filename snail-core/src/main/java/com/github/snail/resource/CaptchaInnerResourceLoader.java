package com.github.snail.resource;

/**
 * @author 		：weiguangyue
 * 
 * 当加载类路径下的资源javascript,css,图片等资源时
 */
public interface CaptchaInnerResourceLoader{

	byte[] loadCss(String name);
	
	byte[] loadJavascript(String name);
	
	byte[] loadHtml(String name);
	
	byte[] loadImage(String name);
	
	byte[] loadResource(String name);

	String getInnerResourceBase();
}
