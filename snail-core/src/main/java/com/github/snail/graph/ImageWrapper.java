package com.github.snail.graph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author weiguangyue
 * 
 * 图片包装
 * 
 */
public class ImageWrapper {

	private final BufferedImage image;
	/**
	 * 2D图片图形
	 */
	private Graphics2D graphics2D;
	/**
	 * 记录选择的区域
	 */
	private final ImageAreaType[][] mapping;

	public ImageWrapper(BufferedImage image,ImageAreaType[][] mapping) {
		super();
		this.mapping = mapping;
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public ImageAreaType[][] getMapping() {
		return mapping;
	}

	public Graphics2D getGraphics2D() {
		if(this.graphics2D == null) {
			this.graphics2D = this.image.createGraphics();
		}
		return graphics2D;
	}
	
	public void disposeGraphics2D() {
		if(this.graphics2D != null) {
			this.graphics2D.dispose();
			this.graphics2D = null;
		}
	}
}
