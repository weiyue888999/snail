package com.github.snail;

import com.github.snail.core.CaptchaController;
import com.github.snail.core.ImageRepository;
import com.github.snail.core.TempFileService;
import com.github.snail.core.Verify;
import com.github.snail.core.VerifyFactory;
import com.github.snail.core.impl.DefaultTempFileServiceImpl;
import com.github.snail.core.impl.DefaultVerifyFactory;
import com.github.snail.core.impl.InnerImageRepository;
import com.github.snail.exceptions.VerifyException;

public class DefaultVerifyCreatorTest {
	
	private TempFileService tempFileService = new DefaultTempFileServiceImpl(10, 10000);
	
	private ImageRepository imageRepository = new InnerImageRepository();
	
	private CaptchaController captchaController = new CaptchaController("login","embedded","zflogin",new MockFilterConfig());
	
	private VerifyFactory verifyFactory = new DefaultVerifyFactory(captchaController,tempFileService);
	
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
	public void test() throws VerifyException{
		Verify verify = this.verifyFactory.create();
		System.out.println(verify);
	}
}
