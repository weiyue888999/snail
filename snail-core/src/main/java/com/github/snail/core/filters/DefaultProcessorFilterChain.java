package com.github.snail.core.filters;

import java.util.ArrayList;
import java.util.List;

import com.github.snail.graph.ImageWrapper;

/**
 * @author 		：weiguangyue
 *
 */
public class DefaultProcessorFilterChain implements ProcessorFilterChain {

	private final List<ProcessorFilter> filters = new ArrayList<ProcessorFilter>();
	private int index = -1;

	@Override
	public void doFilte(ImageWrapper imageWrapper) {
		while (this.index < this.filters.size() - 1) {
			this.filters.get(++this.index).doFilte(imageWrapper, this);
		}
		imageWrapper.disposeGraphics2D();
	}

	@Override
	public void addFilter(ProcessorFilter filter) {
		this.filters.add(filter);
	}
}
