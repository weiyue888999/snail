package com.github.snail.core;

import java.util.Map;

/**
 * @author 		：weiguangyue
 * 
 * 结果渲染
 * 
 */
public interface ResultRender {

	/**
	 * @description	： 当刷新
	 * @param result
	 * @return
	 */
	Map<String,Object> onRefresh(final Map<String,String> result);
	
	/**
	 * @description	： 当验证成功
	 * @param result
	 * @return
	 */
	Map<String,Object> onVerifySuccess(final Map<String,String> result);
	
	/**
	 * @description	： 当验证失败
	 * @param result
	 * @return
	 */
	Map<String,Object> onVerifyFail(final Map<String,String> result);
}
