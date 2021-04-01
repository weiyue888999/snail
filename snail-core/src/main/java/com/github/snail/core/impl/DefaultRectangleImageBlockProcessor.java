package com.github.snail.core.impl;

import java.awt.image.BufferedImage;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ImageBlockProcesseResult;
import com.github.snail.core.ImageBlockProcessor;
import com.github.snail.core.filters.DefaultProcessorFilterChain;
import com.github.snail.core.filters.ProcessorFilterChain;
import com.github.snail.core.filters.background.LogoWatermarkProcessorFilter;
import com.github.snail.core.filters.background.WhiteBackgroundImageFillProcessorFilter;
import com.github.snail.core.filters.block.ImageRangeBorderProcessorFilter;
import com.github.snail.graph.ImageWrapper;
import com.github.snail.graph.ImageWrapperFactory;
import com.github.snail.graph.ImageWrapperPair;
import com.github.snail.graph.Position;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 *
 */
public class DefaultRectangleImageBlockProcessor implements ImageBlockProcessor{
	
	private static final Log log = LogFactory.getLog(DefaultRectangleImageBlockProcessor.class);

	private ImageWrapperFactory imageWrapperFactory;
	
	private CaptchaController captchaController;
	
	public DefaultRectangleImageBlockProcessor(CaptchaController captchaController,ImageWrapperFactory imageWrapperFactory) {
		this.captchaController = captchaController;
		this.imageWrapperFactory = imageWrapperFactory;
	}

	@Override
	public ImageBlockProcesseResult process(BufferedImage originImage) {
		
		int blockSize = this.captchaController.getImageBlockSize();
		int blockCircularRadius = this.captchaController.getImageBlockCircularRadiusSize();
		
		ImageWrapperPair pair = this.imageWrapperFactory.get(originImage,blockSize,blockCircularRadius);
		
		//针对背景图片的处理
		ImageWrapper backgroundImageWrapper = pair.getBackgroundImage();
		{
			ProcessorFilterChain chain = new DefaultProcessorFilterChain();
			
			//白色背景图片填充,模仿网易盾
			chain.addFilter(new WhiteBackgroundImageFillProcessorFilter(blockSize));
			
			//公司文字水印,太low了
			//chain.addFilter(new WordsWatermarkProcessorFilter("正方软件"));
			
			//添加公司logo水印
			if(this.captchaController.isRenderCompanyLogo()){
				chain.addFilter(new LogoWatermarkProcessorFilter());				
			}
			
			chain.doFilte(backgroundImageWrapper);
		}
		
		//针对选择的图片块的处理
		ImageWrapper blockImageWrapper = pair.getBlockImage();
		{
			ProcessorFilterChain chain = new DefaultProcessorFilterChain();
			
			//边缘部分变成白色
			chain.addFilter(new ImageRangeBorderProcessorFilter());
			
			chain.doFilte(blockImageWrapper);
		}
		
		Position p = pair.getPosition();
		return new ImageBlockProcesseResult(backgroundImageWrapper.getImage(),blockImageWrapper.getImage(),p.x,p.y);
	}
	
	
}
