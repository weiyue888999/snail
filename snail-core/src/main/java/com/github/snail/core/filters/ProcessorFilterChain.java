package com.github.snail.core.filters;

import com.github.snail.graph.ImageWrapper;

public interface ProcessorFilterChain {

	void doFilte(final ImageWrapper imageWrapper);
	
	void addFilter(final ProcessorFilter filter);
}
