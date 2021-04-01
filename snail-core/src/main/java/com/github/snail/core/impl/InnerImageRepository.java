package com.github.snail.core.impl;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.github.snail.base.BaseLifecycle;
import com.github.snail.common.Constants;
import com.github.snail.core.ImageRepository;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 *
 */
public class InnerImageRepository extends BaseLifecycle implements ImageRepository{
	
	private static final Log log = LogFactory.getLog(InnerImageRepository.class);
	
	private final String resourceBase = Constants.CAPTCHA_INNER_IMAGE_REGISTRY_RESOURCE_DIR;
	private final String resourceListConfigFile = this.resourceBase + "list.txt";
	
	private final Map<URL,BufferedImage> repository = new LinkedHashMap<URL,BufferedImage>(10);

	@Override
	public BufferedImage put(URL url, BufferedImage bufferedImage) {
		return repository.put(url, bufferedImage);
	}

	@Override
	public BufferedImage remove(URL url) {
		return repository.remove(url);
	}

	@Override
	public BufferedImage get(URL url) {
		return repository.get(url);
	}

	@Override
	public List<URL> list() {
		return Collections.unmodifiableList(new ArrayList<URL>(this.repository.keySet()));
	}

	@Override
	protected void doStart() {
		
		Scanner scanner = null;
		
		try {
			
			log.debug(String.format("load image from list.txt file :%s",resourceListConfigFile));
			InputStream configFileInputStream = this.getClass().getClassLoader().getResourceAsStream(resourceListConfigFile);
			
			//try again
			if(configFileInputStream == null) {
				configFileInputStream = this.getClass().getClassLoader().getResourceAsStream("/"+resourceListConfigFile);	
			}
			scanner = new Scanner(configFileInputStream);
			List<String> resources = new ArrayList<String>();
			while(scanner.hasNext()) {
				resources.add(scanner.next());
			}
			
			Collections.sort(resources);
			
			for(String res : resources) {
				URL url = this.getClass().getClassLoader().getResource(resourceBase+res);
				BufferedImage image = ImageIO.read(url);
				log.debug(String.format("load image %s",url));
				this.put(url,image);
			}
		} catch (Exception e) {
			throw new IllegalStateException("load inner image error,cause : "+e.getMessage(), e);
		}finally {
			if(scanner != null) {
				scanner.close();
			}
		}
	}

	@Override
	protected void doStop() {
		
	}
}
