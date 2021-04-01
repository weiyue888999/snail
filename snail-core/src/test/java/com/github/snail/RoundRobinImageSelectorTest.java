package com.github.snail;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.github.snail.core.SelectResult;
import com.github.snail.core.Selector;
import com.github.snail.core.impl.InnerImageRepository;
import com.github.snail.core.impl.RoundRobinImageSelector;

public class RoundRobinImageSelectorTest {
	
	private InnerImageRepository  repository = new InnerImageRepository();
	
	private Selector<SelectResult<BufferedImage,URL>> selector = new RoundRobinImageSelector(repository);
	
	//@org.junit.Before
	public void start() {
		this.repository.start();
	}

	//@org.junit.Test
	public void testOne() {
		for(int i = 0;i <100;i++) {
			SelectResult<BufferedImage,URL> result = this.selector.select();
			System.out.println(result.getIdentification());
		}
	}
}
