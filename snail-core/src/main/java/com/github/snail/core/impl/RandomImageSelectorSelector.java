package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.SelectResult;
import com.github.snail.core.Selector;

/**
 * @author 		：weiguangyue
 *
 */
public class RandomImageSelectorSelector extends AbstractImageSelector{
	
	private final List<Selector<SelectResult<BufferedImage, URL>>> list = new ArrayList<Selector<SelectResult<BufferedImage, URL>>>(4); 

	public RandomImageSelectorSelector(ImageRepository imageRepository) {
		super(imageRepository);
	}
	
	public RandomImageSelectorSelector(ImageRepository imageRepository,List<Selector<SelectResult<BufferedImage, URL>>> list) {
		super(imageRepository);
		this.list.addAll(list);
	}

	@Override
	public SelectResult<BufferedImage, URL> select() {
		Random random = new Random();
		
		int size = list.size();
		int select = random.nextInt(size);
		return list.get(select).select();
	}

	@Override
	public String getName() {
		return "RandomImageSelectorSelector";
	}
}
