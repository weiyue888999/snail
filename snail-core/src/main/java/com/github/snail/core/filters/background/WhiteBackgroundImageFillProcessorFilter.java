package com.github.snail.core.filters.background;

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
 * 白色图片填充
 */
public class WhiteBackgroundImageFillProcessorFilter implements ProcessorFilter {
	
	private static final BufferedImage BACKGROUND_BLOCK_FILL_IMAGE = new BufferedImage(1, 1,BufferedImage.TYPE_INT_ARGB);
	
	static {
		//白色,透明度150
		Color newcolor = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(),Color.WHITE.getBlue(), 150);
		BACKGROUND_BLOCK_FILL_IMAGE.setRGB(0,0,newcolor.getRGB());
	}
	
	private final int blockSize;
	
	public WhiteBackgroundImageFillProcessorFilter(int blockSize) {
		this.blockSize = blockSize;
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