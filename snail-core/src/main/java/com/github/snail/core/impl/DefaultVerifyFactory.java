package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.github.snail.base.BaseLifecycle;
import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ImageBlockProcesseResult;
import com.github.snail.core.ImageBlockProcessor;
import com.github.snail.core.ImageRepository;
import com.github.snail.core.ImageSelectorFactory;
import com.github.snail.core.SelectResult;
import com.github.snail.core.Selector;
import com.github.snail.core.TempFileService;
import com.github.snail.core.Verify;
import com.github.snail.core.VerifyFactory;
import com.github.snail.exceptions.VerifyException;
import com.github.snail.graph.DefaultImageWrapperFactory;
import com.github.snail.graph.ImageWrapperFactory;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;


/**
@author ：weiguangyue
 
 默认验证生成器
 
 */
public class DefaultVerifyFactory extends BaseLifecycle implements VerifyFactory{

	private static final Log log = LogFactory.getLog(DefaultVerifyFactory.class);
	
	private final CaptchaController captchaController;
	
	private final ImageWrapperFactory imageWrapperFactory = new DefaultImageWrapperFactory();
	
	private ImageRepository imageRepository = new InnerImageRepository();
	
	private ImageSelectorFactory selectorFactory = new DefaultImageSelectorFactory(this.imageRepository);
	
	private TempFileService tempFileService;
	
	private ImageBlockProcessor imageProcessor;
	
	public DefaultVerifyFactory(CaptchaController captchaController,TempFileService tempFileService) {
		this.captchaController = captchaController;
		this.tempFileService = tempFileService;
		this.imageProcessor = new DefaultRectangleImageBlockProcessor(this.captchaController,this.imageWrapperFactory);
	}

	@Override
	public Verify create() throws VerifyException {
		
		String selectorName = this.captchaController.getImageSelectorName();
		Selector<SelectResult<BufferedImage,URL>> selector = this.selectorFactory.get(selectorName);
		if(selector == null) {
			List<String> names = this.selectorFactory.getSelectorNames();
			String nameArray = "[" + Utils.join(names, ",") + "]";
			throw new IllegalStateException("can't find imageSelector["+ selectorName +"] , selectorName must in range " + nameArray);
		}
		
		SelectResult<BufferedImage, URL> selectResult = selector.select();
		
		BufferedImage originImage = selectResult.getResult();
		URL originImageUrl = selectResult.getIdentification();
		
		int originImageHeight = originImage.getHeight();
		int originImageWeight = originImage.getWidth();
		
		int configImageHeight = this.captchaController.getImageHeight();
		int configImageWeight = this.captchaController.getImageWidth();
		
		if(configImageHeight > originImageHeight || configImageWeight > originImageWeight) {
			throw new VerifyException("备选图片不够大");
		}
		
		//使用复制的图片对象
		BufferedImage copyBufferedImage = new BufferedImage(originImage.getWidth(),originImage.getHeight(),originImage.getType());
		copyBufferedImage.setData(originImage.getData());
		
		BufferedImage apaptBufferedImage = copyBufferedImage.getSubimage(0, 0,configImageWeight,configImageHeight);
		
		ImageBlockProcesseResult pickeResult = imageProcessor.process(apaptBufferedImage);
		
		String sifName = UUID.randomUUID().toString() + ".png";
		String mifName = UUID.randomUUID().toString() + ".png";
		
		File sif = tempFileService.create(sifName);
		File mif = tempFileService.create(mifName);
		
		try {
			ImageIO.write(pickeResult.getSi(), "png", sif);
		} catch (IOException e) {
			throw new VerifyException("write image file "+ sif +" error,cause:"+e.getMessage(),e);
		}
		
		try {
			ImageIO.write(pickeResult.getMi(), "png",mif);
		} catch (IOException e) {
			throw new VerifyException("write image file "+ mif +" error,cause:"+e.getMessage(), e);
		}
		
		Verify verify = new DefaultVerify(this.captchaController.getInstanceId(),sifName,sif,mifName,mif,pickeResult.getX(),pickeResult.getY());
		return verify;
	}

	@Override
	protected void doStart() {
		this.imageRepository.start();
	}

	@Override
	protected void doStop() {
		this.imageRepository.stop();
	}
}
