package com.github.snail.core;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

/**
 * @author 		：weiguangyue
 * 
 * 图片选择器工厂
 */
public interface ImageSelectorFactory {

	/**
	 * 得到一个图片选择器
	 * @param name
	 * @return
	 */
	Selector<SelectResult<BufferedImage,URL>> get(String name);
	
	/**
	 * 得到所有选择器的名字
	 * @return
	 */
	List<String> getSelectorNames();
}
