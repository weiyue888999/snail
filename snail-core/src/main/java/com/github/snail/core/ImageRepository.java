package com.github.snail.core;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import com.github.snail.base.Lifecycle;

public interface ImageRepository extends Lifecycle{

	BufferedImage put(URL url,BufferedImage bufferedImage);
	
	BufferedImage remove(URL url);
	
	BufferedImage get(URL url);
	
	List<URL> list();
}
