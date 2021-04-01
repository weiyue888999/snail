package com.github.snail;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ImageBlockProcesseResult;
import com.github.snail.core.ImageBlockProcessor;
import com.github.snail.core.ImageRepository;
import com.github.snail.core.TempFileService;
import com.github.snail.core.impl.DefaultRectangleImageBlockProcessor;
import com.github.snail.core.impl.DefaultTempFileServiceImpl;
import com.github.snail.core.impl.InnerImageRepository;
import com.github.snail.graph.DefaultImageWrapperFactory;

public class RectangleImageProcessorTest {
	
	private TempFileService tempFileService = new DefaultTempFileServiceImpl(10, 10000);
	
	private ImageRepository imageRepository = new InnerImageRepository();
	
	private CaptchaController captchaController = new CaptchaController("login","embedded","zflogin",new MockFilterConfig());
	
	private ImageBlockProcessor picker = new DefaultRectangleImageBlockProcessor(captchaController,new DefaultImageWrapperFactory());
	
	//@org.junit.Before
	public void startup() {
		this.tempFileService.start();
		this.imageRepository.start();
	}
	
	//@org.junit.After
	public void shutdonw() {
		this.imageRepository.stop();
		this.tempFileService.stop();
	}

	//@org.junit.Test
	public void test() {
		try {
			URL url = imageRepository.list().get(0);
			BufferedImage srcImage = imageRepository.get(url);
			ImageBlockProcesseResult result = picker.process(srcImage);
			String type = Utils.getExtension(url.getFile());
			ImageIO.write(result.getSi(), type, this.tempFileService.create(type));
			ImageIO.write(result.getMi(), type, this.tempFileService.create(type));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
