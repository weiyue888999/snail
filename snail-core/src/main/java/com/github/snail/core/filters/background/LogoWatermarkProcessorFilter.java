package com.github.snail.core.filters.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.github.snail.core.filters.ProcessorFilter;
import com.github.snail.core.filters.ProcessorFilterChain;
import com.github.snail.graph.ImageWrapper;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 *
 */
public class LogoWatermarkProcessorFilter implements ProcessorFilter{
	
	private static final Log log = LogFactory.getLog(LogoWatermarkProcessorFilter.class);
	
	private static BufferedImage LOGO_IMAGE = null;
	
	static {
		URL url = LogoWatermarkProcessorFilter.class.getClassLoader().getResource("com/github/snail/core/filters/background/logo-white-small.png");
		if(url != null) {
			try {
				LOGO_IMAGE = ImageIO.read(url);
			} catch (IOException e) {
				log.error("",e);
				throw new IllegalStateException("load image["+ url +"]error!!!");
			}			
		}
	}

	@Override
	public void doFilte(ImageWrapper imageWrapper, ProcessorFilterChain chain) {
		
		if(LOGO_IMAGE != null) {
			
			Graphics2D graphics2D = imageWrapper.getGraphics2D();
			graphics2D.drawImage(LOGO_IMAGE, 2, 2, null);
		}
		
		chain.doFilte(imageWrapper);
	}
}
