package com.github.snail.core.filters;

import com.github.snail.graph.ImageWrapper;

/**
 * @author 		：weiguangyue
 *
 */
public interface ProcessorFilter {

	void doFilte(final ImageWrapper imageWrapper,final ProcessorFilterChain chain);
}
