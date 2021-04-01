package com.github.snail.core.filters.block;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.github.snail.core.filters.ProcessorFilter;
import com.github.snail.core.filters.ProcessorFilterChain;
import com.github.snail.graph.ImageAreaType;
import com.github.snail.graph.ImageWrapper;

/**
 * @author 		：weiguangyue
 *
 */
public class ImageRangeBorderProcessorFilter implements ProcessorFilter{
	
	private static BufferedImage BACKGROUND_BLOCK_FILL_IMAGE = new BufferedImage(1, 1,BufferedImage.TYPE_INT_ARGB);
	{
		//白色,透明度50
		Color newcolor = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(),Color.WHITE.getBlue(), 50);
		BACKGROUND_BLOCK_FILL_IMAGE.setRGB(0,0,newcolor.getRGB());
	}
	
	public ImageRangeBorderProcessorFilter() {
	}

	@Override
	public void doFilte(ImageWrapper imageWrapper, ProcessorFilterChain chain) {
		
		ImageAreaType[][] mapping = imageWrapper.getMapping();

		Graphics2D graphics2D = imageWrapper.getGraphics2D();
		for (int x = 0; x < mapping.length; x++) {
			for (int y = 0; y < mapping[x].length; y++) {
				if (mapping[x][y] == ImageAreaType.SELECT) {
					graphics2D.drawImage(BACKGROUND_BLOCK_FILL_IMAGE, x, y, null);
				}
			}
		}
		chain.doFilte(imageWrapper);
	}
}
