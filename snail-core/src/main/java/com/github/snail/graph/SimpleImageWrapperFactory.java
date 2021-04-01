package com.github.snail.graph;

import java.awt.image.BufferedImage;

/**
 * @author 		：weiguangyue
 */
class SimpleImageWrapperFactory implements ImageWrapperFactory {

	@Override
	public ImageWrapperPair get(BufferedImage originImage,int blockSize,int blockCircularRadius) {

		// 在这个区域选择一个坐标
		Position p = GraphUtils.createRandomPosition(originImage.getWidth(), originImage.getHeight(), blockSize);

		// 随机选择一个矩形
		Rectangle rectangle = GraphUtils.createRectangle(p, blockSize, blockSize);

		// 得到块图片
		ImageWrapper blockImageWrapper = getBlockImageWrapper(rectangle, originImage);
		processBlockImage(blockImageWrapper, rectangle);

		// 处理已经挖去块的原始图片
		ImageWrapper backgroundImageWrapper = getBackgroundImageWrapper(rectangle, originImage);

		return new ImageWrapperPair(p,backgroundImageWrapper, blockImageWrapper);
	}

	private ImageWrapper getBlockImageWrapper(Rectangle rectangle, BufferedImage originImage) {
		
		ImageAreaType[][] mapping = new ImageAreaType[rectangle.getWidth()][originImage.getHeight()];
		init(mapping);

		BufferedImage blockImage = new BufferedImage(rectangle.getWidth(), originImage.getHeight(),BufferedImage.TYPE_INT_ARGB);

		int targetMinX = rectangle.getA().x;
		int targetMaxX = rectangle.getB().x;
		int targetMinY = rectangle.getA().y;
		int targetMaxY = rectangle.getC().y;

		int startX = rectangle.getA().x;
		int startY = rectangle.getA().y;

		int originMaxX = originImage.getWidth();
		int originImageMaxY = originImage.getHeight();

		for (int x = 0; x < originMaxX; x++) {
			for (int y = 0; y < originImageMaxY; y++) {
				boolean inRange = targetMinX <= x && x < targetMaxX && targetMinY <= y && y < targetMaxY;
				if (inRange) {
					int originImageRgb = originImage.getRGB(x, y);
					int targetX = x - startX;
					
					blockImage.setRGB(targetX, y, originImageRgb);
					mapping[targetX][y] = ImageAreaType.SELECT;
				}
			}
		}
		return new ImageWrapper(blockImage,mapping);
	}

	private void init(ImageAreaType[][] mapping) {
		for(int x = 0; x < mapping.length;x++) {
			for(int y = 0;y<mapping[x].length;y++) {
				mapping[x][y] = ImageAreaType.UNSELECT;
			}
		}
	}

	private ImageWrapper getBackgroundImageWrapper(Rectangle rectangle, BufferedImage originImage) {
		
		ImageAreaType[][] mapping = new ImageAreaType[originImage.getWidth()][originImage.getHeight()];
		init(mapping);

		int targetMinX = rectangle.getA().x;
		int targetMaxX = rectangle.getB().x;
		int targetMinY = rectangle.getA().y;
		int targetMaxY = rectangle.getC().y;

		int originMaxX = originImage.getWidth();
		int originImageMaxY = originImage.getHeight();

		for (int x = 0; x < originMaxX; x++) {
			for (int y = 0; y < originImageMaxY; y++) {
				boolean inRange = targetMinX <= x && x < targetMaxX && targetMinY <= y && y < targetMaxY;
				if (inRange) {
					mapping[x][y] = ImageAreaType.SELECT;
				}
			}
		}
		return new ImageWrapper(originImage,mapping);
	}

	private void processBlockImage(ImageWrapper blockImageWrapper, Rectangle rectangle) {

		BufferedImage image = blockImageWrapper.getImage();
		// 针对结果图片，不是指定的矩形的地方，填充成白色透明色
		int y1 = rectangle.getA().y;
		int y2 = rectangle.getC().y;

		for (int x = 0; x < image.getWidth(); x++) {

			for (int y = 0; y < image.getHeight(); y++) {

				boolean inRange = y1 <= y && y < y2;
				if (!inRange) {
					image.setRGB(x, y, GraphUtils.ARGB_WHITE_TRANSPARENT);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SimpleImageWrapperFactory";
	}
}
