package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.snail.core.ImageRepository;
import com.github.snail.core.ImageSelectorFactory;
import com.github.snail.core.SelectResult;
import com.github.snail.core.Selector;

/**
 * @author 		：weiguangyue
 * 
 * 默认图片选择器工厂
 */
public class DefaultImageSelectorFactory implements ImageSelectorFactory{
	
	private final Map<String,Selector<SelectResult<BufferedImage, URL>>> mapping = new HashMap<String,Selector<SelectResult<BufferedImage, URL>>>(4);
	
	public DefaultImageSelectorFactory(ImageRepository imageRepository) {
		{
			Selector<SelectResult<BufferedImage, URL>> selector = new RandomImageSelector(imageRepository);
			this.mapping.put(selector.getName(), selector);
		}
		
		{
			Selector<SelectResult<BufferedImage, URL>> selector = new RoundRobinImageSelector(imageRepository);
			this.mapping.put(selector.getName(), selector);
		}
		
		{
			Selector<SelectResult<BufferedImage, URL>> selector = new SelectFirstImageSelector(imageRepository);
			this.mapping.put(selector.getName(), selector);
		}
		
		{
			//这个放最后
			Selector<SelectResult<BufferedImage, URL>> selector = new RandomImageSelectorSelector(imageRepository,new ArrayList<Selector<SelectResult<BufferedImage, URL>>>(this.mapping.values()));
			this.mapping.put(selector.getName(), selector);
		}
	}

	@Override
	public Selector<SelectResult<BufferedImage, URL>> get(String name) {
		return this.mapping.get(name);
	}

	@Override
	public List<String> getSelectorNames() {
		return Collections.unmodifiableList(new ArrayList<String>(this.mapping.keySet()));
	}
}
