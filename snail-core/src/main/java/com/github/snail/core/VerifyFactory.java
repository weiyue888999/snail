package com.github.snail.core;

import com.github.snail.base.Lifecycle;
import com.github.snail.exceptions.VerifyException;

/**
 * @author 		：weiguangyue
 */
public interface VerifyFactory extends Lifecycle{
	/**
	 * @description	： 创建验证对象
	 * @return
	 */
	Verify create() throws VerifyException;
}
