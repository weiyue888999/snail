package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.SelectResult;

/**
 * @author 		：weiguangyue
 */
public class RoundRobinImageSelector extends AbstractImageSelector{
	
	private int index = -1;

	public RoundRobinImageSelector(ImageRepository imageRepository) {
		super(imageRepository);
	}

	@Override
	public SelectResult<BufferedImage, URL> select() {
		
		return this.doSelect(nextIndex());
	}
	
	private synchronized int nextIndex() {
		this.index ++;
		if(this.index > this.getImageRepository().list().size() - 1) {
			this.index = 0;
		}
		return this.index;
	}

	@Override
	public String getName() {
		return "RoundRobinImageSelector";
	}
}
