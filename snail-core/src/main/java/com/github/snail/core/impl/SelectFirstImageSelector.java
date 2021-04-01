package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.SelectResult;

/**
 * @author 		：weiguangyue
 */
public class SelectFirstImageSelector extends AbstractImageSelector{

	@Override
	public String getName() {
		return "SelectFirstImageSelector";
	}
	
	public SelectFirstImageSelector(ImageRepository imageRepository) {
		super(imageRepository);
	}

	@Override
	public SelectResult<BufferedImage, URL> select() {
		
		return this.doSelect(0);
	}

}
