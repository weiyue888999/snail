package com.github.snail.core.filters.background;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.github.snail.core.filters.ProcessorFilter;
import com.github.snail.core.filters.ProcessorFilterChain;
import com.github.snail.graph.ImageWrapper;

/**
 * @author 		：weiguangyue
 *
 */
public class WordsWatermarkProcessorFilter implements ProcessorFilter{
	
	private final String watermark;

	public WordsWatermarkProcessorFilter(String watermark) {
		this.watermark = watermark;
	}

	@Override
	public void doFilte(ImageWrapper imageWrapper, ProcessorFilterChain chain) {
		
		Graphics2D graphics2D = imageWrapper.getGraphics2D();
		graphics2D.setFont(new Font("新宋体",Font.BOLD,12));
		graphics2D.setColor(Color.RED);
		graphics2D.drawString(watermark, 20, 20);
		
		chain.doFilte(imageWrapper);
	}
}
