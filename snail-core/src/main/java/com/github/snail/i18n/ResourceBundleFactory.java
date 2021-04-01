package com.github.snail.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import com.github.snail.common.Constants;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

public class ResourceBundleFactory {
	
	private static final Log log = LogFactory.getLog(ResourceBundleFactory.class);
	
	private ResourceBundleFactory() {
	}
	
	public static void set(HttpSession session,String language) {
		
		if("en-US".equals(language) || "en_US".equals(language) || "zh-CN".equals(language) || "zh_CN".equals(language)) {
			log.debug("session : "+session.getId()+" change language to "+language);
			session.setAttribute(Constants.LANGUAGE_LOCAL_SESSION_KEY,language);
		}else {
			log.warn("use defaultLocale cause can't find languageLocal:["+ language +"],please contact with 基础平台部");
		}
	}

	public static ResourceBundle get(HttpSession session) {
		
		String languageLocal = (String) session.getAttribute(Constants.LANGUAGE_LOCAL_SESSION_KEY);
		Locale locale = null;
		if(languageLocal == null) {
			locale = Locale.getDefault();
		}else {
			if("en-US".equals(languageLocal) || "en_US".equals(languageLocal)) {
				locale = Locale.US;
			}else if("zh-CN".equals(languageLocal) || "zh_CN".equals(languageLocal)) {
				locale = Locale.SIMPLIFIED_CHINESE;
			}else {
				log.warn("use defaultLocale cause can't find languageLocal:["+ languageLocal +"],please contact with 基础平台部");
				locale = Locale.getDefault();
			}
		}
		ResourceBundle result = ResourceBundle.getBundle("message/i18n/snail", locale,ResourceBundleFactory.class.getClassLoader());
		
		return result;
	}
}
