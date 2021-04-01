package com.github.snail.decoder.impl;

import java.io.UnsupportedEncodingException;

import com.github.snail.common.Base64;
import com.github.snail.decoder.TracePositionTraceDecoder;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.resource.CacheableCaptchaInnerResourceLoader;
import com.github.snail.resource.CaptchaInnerResourceLoader;

/**
 * @author 		：weiguangyue
 * 
 * base 64 解密器
 */
public class Base64TracePositionTraceDecoder implements TracePositionTraceDecoder{
	
	private static final Log log = LogFactory.getLog(Base64TracePositionTraceDecoder.class);
	
	private static final String RESOURCE_NAME = Base64TracePositionTraceDecoder.class.getSimpleName()+".js";
	
	//private CaptchaInnerResourceLoader loader = new DefaultCaptchaInnerResourceLoader("com/github/snail/decoder/impl/");
	private CaptchaInnerResourceLoader loader = new CacheableCaptchaInnerResourceLoader("com/github/snail/decoder/impl/");
	
	@Override
	public String getName() {
		return "base64";
	}
	
	@Override
	public String decode(String encryptedStr) {
		return new String(Base64.base64ToByteArray(encryptedStr));
	}

	@Override
	public String javascriptEncodeCode() {
		String javascript;
		try {
			javascript = new String(this.loader.loadResource(RESOURCE_NAME),"UTF-8");
			return javascript;
		} catch (UnsupportedEncodingException e) {
			log.error("",e);
		}
		return "";
	}
}
