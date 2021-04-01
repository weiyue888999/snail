package com.github.snail.graph;

/**
 * @author 		：weiguangyue
 * 
 * 图片封装对
 * 
 */
public class ImageWrapperPair {
	/**
	 * 位置，此位置是块的左上角在背景图片当中的位置
	 */
	private final Position position;
	/**
	 * 背景图片
	 */
	private final ImageWrapper backgroundImage;
	/**
	 * 块图片
	 */
	private final ImageWrapper blockImage;

	public ImageWrapperPair(Position position, ImageWrapper backgroundImage, ImageWrapper blockImage) {
		super();
		this.position = position;
		this.backgroundImage = backgroundImage;
		this.blockImage = blockImage;
	}

	public Position getPosition() {
		return position;
	}

	public ImageWrapper getBackgroundImage() {
		return backgroundImage;
	}

	public ImageWrapper getBlockImage() {
		return blockImage;
	}
	
}
