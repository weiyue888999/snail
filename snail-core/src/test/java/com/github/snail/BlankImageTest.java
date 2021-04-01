package com.github.snail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class BlankImageTest {

	//@org.junit.Test
	public void test() throws Exception{
		int size = 60;
		BufferedImage blockImage = new BufferedImage(size, size,BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < size ;x++) {
			for(int y = 0;y < size ;y++) {
				Color newcolor = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(),Color.WHITE.getBlue(), 150);
				blockImage.setRGB(x,y,newcolor.getRGB());
			}
		}
		BufferedImage bk = ImageIO.read(new File("c:\\background.png"));
		Graphics2D graphics2D = bk.createGraphics();
		graphics2D.drawImage(blockImage, 0, 0, null);
		ImageIO.write(bk, "png", new File("C:\\result.png"));
		ImageIO.write(blockImage, "png", new File("C:\\1.png"));

	}
}
