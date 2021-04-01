package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.SelectResult;
import com.github.snail.core.Selector;

/**
 * @author 		：weiguangyue
 *
 */
public abstract class AbstractImageSelector implements Selector<SelectResult<BufferedImage,URL>>{

	private final ImageRepository imageRepository;
	
	public AbstractImageSelector(ImageRepository imageRepository) {
		super();
		this.imageRepository = imageRepository;
	}
	
	protected SelectResult<BufferedImage, URL> doSelect(int index) {
		
		URL url = this.getImageRepository().list().get(index);
		
		BufferedImage result = this.getImageRepository().get(url);
		
		return new DefaultImageSelectResult(result,url);
	}

	protected ImageRepository getImageRepository() {
		return imageRepository;
	}
}
