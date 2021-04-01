package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Random;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.SelectResult;

/**
 * @author 		：weiguangyue
 *
 */
public class RandomImageSelector extends AbstractImageSelector{
	
	public RandomImageSelector(ImageRepository imageRepository) {
		super(imageRepository);
	}

	@Override
	public SelectResult<BufferedImage,URL> select() {
		
		Random random = new Random();
		
		List<URL> list = this.getImageRepository().list();
		int size = list.size();
		int select = random.nextInt(size);
		
		return this.doSelect(select);
		
	}

	@Override
	public String getName() {
		return "RandomImageSelector";
	}
}
