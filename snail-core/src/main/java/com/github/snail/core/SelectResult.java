package com.github.snail.core;

/**
 * @author 		：weiguangyue
 *
 */
public interface SelectResult<R/*结果*/,D/*唯一标识*/> {

	R getResult();
	
	D getIdentification();
}
