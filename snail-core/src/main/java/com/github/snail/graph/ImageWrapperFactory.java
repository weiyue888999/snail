package com.github.snail.graph;

import java.awt.image.BufferedImage;

/**
 * @author 		：weiguangyue
 * 
 * ImageWrapper工厂
 */
public interface ImageWrapperFactory {
	/**
	 * @param image 原始图片
	 * @param blockSize 块大小
	 * @return 返回处理后的2个图片的封装对象
	 */
	ImageWrapperPair get(BufferedImage originImage,int blockSize,int blockCircularRadius);
	
	String getName();
}
