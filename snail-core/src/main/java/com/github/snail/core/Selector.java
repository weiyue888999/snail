package com.github.snail.core;

/**
 * @author 		：weiguangyue
 */
public interface Selector<R> {
	
	String getName();

	R select();
}
