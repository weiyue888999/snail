package com.github.snail.core;

import java.awt.image.BufferedImage;

/**
 * @author 		：weiguangyue
 */
public class ImageBlockProcesseResult {
	/**
	 * 原始图片
	 */
	private final BufferedImage si;
	/**
	 * 块图片
	 */
	private final BufferedImage mi;
	/**
	 * 选取块的横坐标
	 */
	private final int x;
	/**
	 * 选取块的纵坐标
	 */
	private final int y;

	public ImageBlockProcesseResult(BufferedImage si, BufferedImage mi, int x, int y) {
		super();
		this.si = si;
		this.mi = mi;
		this.x = x;
		this.y = y;
	}

	public BufferedImage getSi() {
		return si;
	}

	public BufferedImage getMi() {
		return mi;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
