package com.github.snail.graph;

import java.awt.image.BufferedImage;

public abstract class BaseImageWrapperFactory implements ImageWrapperFactory{

	@Override
	public ImageWrapperPair get(BufferedImage originImage,int blockSize,int blockCircularRadius) {

		// 在这个区域生产一个随机坐标
		Position p = GraphUtils.createRandomPosition(originImage.getWidth(), originImage.getHeight(), blockSize+blockCircularRadius);

		// 生产一个矩形
		Rectangle rectangle = GraphUtils.createRectangle(p, blockSize, blockSize);
		
		//原始图片的块选择区域
		ImageAreaType[][] originImageSelectBlockArea = new ImageAreaType[originImage.getWidth()+blockCircularRadius][originImage.getHeight()];
		GraphUtils.initImageAreaType(originImageSelectBlockArea);
		
		//拷贝图片
		//blockImage
		//使用复制的图片对象
		BufferedImage copyBufferedImage = new BufferedImage(originImage.getWidth(),originImage.getHeight(),originImage.getType());
		copyBufferedImage.setData(originImage.getData());
		
		//减去左边和右边后的图片
		BufferedImage cutBufferedImage = copyBufferedImage.getSubimage(rectangle.getA().x, 0,blockSize+blockCircularRadius,originImage.getHeight());
		BufferedImage resultBlockImage = new BufferedImage(cutBufferedImage.getWidth(), cutBufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int x = 0;x<cutBufferedImage.getWidth();x++) {
			for(int y = 0;y<cutBufferedImage.getHeight();y++) {
				resultBlockImage.setRGB(x, y, cutBufferedImage.getRGB(x, y));
			}	
		}
		
		//块图片的选择区域
		ImageAreaType[][] blockImageSelectBlockArea = new ImageAreaType[blockSize+blockCircularRadius][originImage.getHeight()];
		GraphUtils.initImageAreaType(blockImageSelectBlockArea);
		
		//处理填充原始图片和块图片的选择区域
		this.processSelectArea(blockCircularRadius,rectangle,originImageSelectBlockArea,blockImageSelectBlockArea);
		
		// 处理已经挖去块的原始图片
		ImageWrapper backgroundImageWrapper = getBackgroundImageWrapper(originImageSelectBlockArea, originImage);
		processOriginImage(backgroundImageWrapper);
		
		// 得到块图片
		ImageWrapper blockImageWrapper = getBlockImageWrapper(blockImageSelectBlockArea, resultBlockImage);
		processBlockImageEmptyArea(blockImageWrapper);
		processBlockImage(blockImageWrapper);

		return new ImageWrapperPair(p,backgroundImageWrapper, blockImageWrapper);
	}

	private void processBlockImageEmptyArea(ImageWrapper blockImageWrapper) {
		
		BufferedImage image = blockImageWrapper.getImage();
		ImageAreaType[][] area = blockImageWrapper.getMapping();
		
		// 针对结果图片，不是指定的区域的地方，填充成白色透明色
		for (int x = 0; x < area.length; x++) {

			for (int y = 0; y < area[x].length; y++) {

				if(area[x][y] == ImageAreaType.UNSELECT) {
					image.setRGB(x, y, GraphUtils.ARGB_WHITE_TRANSPARENT);
				}
			}
		}
	}

	protected abstract void processOriginImage(ImageWrapper backgroundImageWrapper);
	
	protected abstract void processBlockImage(ImageWrapper backgroundImageWrapper);

	/**
	 * @description	： 处理块选择区域
	 * @param rectangle
	 * @param originImageSelectBlockArea
	 * @param blockImageSelectBlockArea
	 */
	private void processSelectArea(int radius,Rectangle rectangle,ImageAreaType[][] originImageSelectBlockArea, ImageAreaType[][] blockImageSelectBlockArea) {
		
		//矩形区域选择
		
		//(x-a)²+(y-b)²=r²
		//a和b是圆心,x和y是坐标
		//首先处理背景图片的选择问题
	 	
	 	//处理上边的小圆形块
	 	{
	 		//圆型小块的坐标
	 		Position mid = GraphUtils.midXPosition(rectangle.getA(), rectangle.getB());
	 		
	 		GraphUtils.populateSelectCircularArea(radius, mid, originImageSelectBlockArea);
	 	}
	 	
	 	//处理右边的小圆块
	 	{
	 		//圆型小块的坐标
	 		Position mid = GraphUtils.midYPosition(rectangle.getB(), rectangle.getD());
	 		
	 		GraphUtils.populateSelectCircularArea(radius, mid, originImageSelectBlockArea);
	 	}
	 	
	 	//选择矩形区域
	 	{
	 		GraphUtils.populateSelectRectangleArea(rectangle,originImageSelectBlockArea);
	 	}
	 	
	 	//复制一个矩形，使其平移到(0,y)位置，这样就可以用来操作块图片的矩形了
	 	Rectangle rectangleInBlockImage = GraphUtils.moveToLeft(rectangle);
	 	
	 	//处理上边的小圆形块
	 	{
	 		//圆型小块的坐标
	 		Position mid = GraphUtils.midXPosition(rectangleInBlockImage.getA(), rectangleInBlockImage.getB());
	 		
	 		GraphUtils.populateSelectCircularArea(radius, mid, blockImageSelectBlockArea);
	 	}
	 	
	 	//处理右边的小圆块
	 	{
	 		//圆型小块的坐标
	 		Position mid = GraphUtils.midYPosition(rectangleInBlockImage.getB(), rectangleInBlockImage.getD());
	 		
	 		GraphUtils.populateSelectCircularArea(radius, mid, blockImageSelectBlockArea);
	 	}
	 	
	 	//选择矩形区域
	 	{
	 		GraphUtils.populateSelectRectangleArea(rectangleInBlockImage,blockImageSelectBlockArea);

	 	}
	}

	private ImageWrapper getBlockImageWrapper(ImageAreaType[][] blockImageSelectBlockArea, BufferedImage blockImage) {
		
		return new ImageWrapper(blockImage,blockImageSelectBlockArea);
	}

	private ImageWrapper getBackgroundImageWrapper(ImageAreaType[][] originImageSelectBlockArea, BufferedImage originImage) {
		
		return new ImageWrapper(originImage,originImageSelectBlockArea);
	}

	@Override
	public String getName() {
		return "BaseImageWrapperFactory";
	}
}
