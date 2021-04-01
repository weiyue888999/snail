package com.github.snail.verify;

/**
 * 验证结果
 */
public interface VerifyResult {

	/**
	 * 是否成功
	 * @return
	 */
	boolean success();

	/**
	 * 失败原因
	 * @return
	 */
	String getFailReason();
}
