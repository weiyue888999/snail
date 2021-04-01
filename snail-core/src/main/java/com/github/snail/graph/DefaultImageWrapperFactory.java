package com.github.snail.graph;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DefaultImageWrapperFactory extends BaseImageWrapperFactory {

	@Override
	public String getName() {
		return "DefaultImageWrapperFactory";
	}

	@Override
	protected void processOriginImage(ImageWrapper backgroundImageWrapper) {

	}

	@Override
	protected void processBlockImage(ImageWrapper backgroundImageWrapper) {
		BufferedImage image = backgroundImageWrapper.getImage();
		ImageAreaType[][] borders = GraphUtils.getSelectBorder(backgroundImageWrapper);
		for (int x = 0; x < borders.length; x++) {
			for (int y = 0; y < borders[x].length; y++) {
				if(borders[x][y] == ImageAreaType.SELECT) {
					image.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
	}
}
