package com.github.snail.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 *
 */
public class DefaultCaptchaInnerResourceLoader implements CaptchaInnerResourceLoader{
	
	private static final Log log = LogFactory.getLog(DefaultCaptchaInnerResourceLoader.class);
	
	private String innerResourceBase = Constants.CAPTCHA_INNER_RESOURCE_DIR_CAPTCHA;
	
	public DefaultCaptchaInnerResourceLoader(String innerResourceBase) {
		super();
		this.innerResourceBase = innerResourceBase;
	}

	@Override
	public String getInnerResourceBase() {
		return innerResourceBase;
	}

	@Override
	public byte[] loadCss(String name) {
		return this.doLoadResource("css/",name);
	}

	@Override
	public byte[] loadJavascript(String name) {
		return this.doLoadResource("javascript/",name);
	}

	@Override
	public byte[] loadHtml(String name) {
		return this.doLoadResource("html/",name);
	}

	@Override
	public byte[] loadImage(String name) {
		return this.doLoadResource("image/",name);
	}
	
	public byte[] loadResource(String name) {
		return this.doLoadResource("", name);
	}
	
	protected byte[] doLoadResource(String resourceDir, String name) {
		String resourcePath = this.innerResourceBase + resourceDir + name;
		InputStream inputStream = null;
		try {
			URL url = this.getClass().getClassLoader().getResource(resourcePath);
			
			//try again
			if(url == null) {
				url = this.getClass().getClassLoader().getResource("/"+resourcePath);
			}
			try {
				inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
				log.debug(String.format("load resource :%s for path :%s",name,url));
				
				byte[] byteArray = Utils.toByteArray(inputStream);
				return byteArray;
				
			}catch (FileNotFoundException e) {
				throw new RuntimeException("can't find resource "+name+" for path:"+resourcePath+",cause "+e.getMessage(),e);
			}finally {
				Utils.closeQuietly(inputStream);
			}
		}catch (Exception e) {
			throw new RuntimeException("load "+name+" error ,cause : "+e.getMessage(),e);
		}
	}
}
