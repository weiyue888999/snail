package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.github.snail.core.SelectResult;

/**
 * @author 		：weiguangyue
 *
 */
class DefaultImageSelectResult implements SelectResult<BufferedImage,URL>{
	
	private final BufferedImage bufferedImage;
	
	private final URL identification;
	
	public DefaultImageSelectResult(BufferedImage bufferedImage, URL identification) {
		this.bufferedImage = bufferedImage;
		this.identification = identification;
	}

	@Override
	public BufferedImage getResult() {
		return bufferedImage;
	}
	
	@Override
	public URL getIdentification() {
		return identification;
	}
}
