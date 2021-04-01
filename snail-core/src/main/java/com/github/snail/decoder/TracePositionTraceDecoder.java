package com.github.snail.decoder;

/**
 * @author 		：weiguangyue
 * 
 * 轨迹解密器
 */
public interface TracePositionTraceDecoder {
	
	/**
	 * @description	： 这个名称
	 * @return
	 */
	String getName();

	/**
	 * @param encryptedStr 加密的字符串
	 * @return 解密后的字符串
	 */
	String decode(String encryptedStr);
	
	/**
	 * @description	： 获得javascript加密代码
	 * @return
	 */
	String javascriptEncodeCode();
}
