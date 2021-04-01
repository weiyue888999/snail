package com.github.snail.resource;

import java.util.HashMap;
import java.util.Map;

import com.github.snail.common.Utils;

/**
 * @author 		：weiguangyue
 * 
 * 可缓存资源的内部资源加载器
 */
public class CacheableCaptchaInnerResourceLoader extends DefaultCaptchaInnerResourceLoader{
	
	private static final Map<String,byte[]> RESOURCE_CACHE = new HashMap<String, byte[]>(11);
	
	public CacheableCaptchaInnerResourceLoader(String innerResourceBase) {
		super(innerResourceBase);
	}

	@Override
	protected byte[] doLoadResource(String resourceDir, String name) {
		String key = getInnerResourceBase() + resourceDir + name;
		byte[] array = RESOURCE_CACHE.get(key);
		if(array == null) {
			synchronized (RESOURCE_CACHE) {
				array = RESOURCE_CACHE.get(key);
				if(array == null) {
					array = super.doLoadResource(resourceDir, name);
					RESOURCE_CACHE.put(key, array);					
				}
			}
		}
		return Utils.cloneByteArry(array);
	}
}
