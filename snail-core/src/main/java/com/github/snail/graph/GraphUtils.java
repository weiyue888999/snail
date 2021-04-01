package com.github.snail.graph;

import java.awt.Color;
import java.util.Random;

/**
@author weiguangyue
 
**/
public class GraphUtils {
	
	/**
	 * 白色透明
	 */
	static final int ARGB_WHITE_TRANSPARENT = (0 << 24) | (Color.WHITE.getRGB() & 0x00ffffff);

	/**
	 * @description	： 生产一个矩形
	 * @param position
	 * @param height
	 * @param width
	 * @return
	 */
	public static Rectangle createRectangle(Position position,int height,int width) {
		
		Position a = position;
		Position b = new Position(a.x + width,a.y);
		Position c = new Position(a.x ,a.y + height);
		Position d = new Position(a.x + width, a.y + height);
		
		return new Rectangle(a, b, c, d);
	}
	
	public static void initImageAreaType(ImageAreaType[][] mapping) {
		for(int x = 0; x < mapping.length;x++) {
			for(int y = 0;y<mapping[x].length;y++) {
				mapping[x][y] = ImageAreaType.UNSELECT;
			}
		}
	}
	
	public static Position midXPosition(Position a,Position b) {
		return new Position((a.x + b.x)/2, a.y);
	}
	
	public static Position midYPosition(Position a,Position b) {
		return new Position(a.x, (a.y + b.y)/2);
	}
	
	public static void populateSelectCircularArea(int r,Position position,ImageAreaType[][] imageArea) {
 		for(int x = 0;x < imageArea.length;x++) {
 			for(int y = 0;y < imageArea[x].length;y++) {
 				int rr = (x - position.x)*(x - position.x) + (y - position.y)*(y - position.y);
 				if(rr <= r*r) {
 					imageArea[x][y] = ImageAreaType.SELECT;
 				}
 			}
 		}
	}
	
	public static void populateSelectRectangleArea(Rectangle rectangle,ImageAreaType[][] imageArea) {
 		for(int x = rectangle.getA().x;x <= rectangle.getD().x;x++) {
 			for(int y = rectangle.getA().y;y <= rectangle.getD().y ;y++) {
 				imageArea[x][y] = ImageAreaType.SELECT;
 			}
 		}
	}
	
	/**
	 * @description	： 将矩形平移到最左边
	 * @param rectangle
	 * @return
	 */
	public static Rectangle moveToLeft(Rectangle rectangle) {
		int moveDistance = rectangle.getA().x;
		Position a = new Position(0, rectangle.getA().y);
		Position b = new Position(rectangle.getB().x - moveDistance,rectangle.getB().y);
		Position c = new Position(0,rectangle.getC().y);
		Position d = new Position(rectangle.getD().x- moveDistance,rectangle.getD().y);
		return new Rectangle(a, b, c, d);
	}
	
	public static int getRandomInt(int n) {
		Random random = new Random();
		return random.nextInt(n);
	}
	
	public static int getRandomInt(int min,int max) {
		int x = 0;
		do {
			x = getRandomInt(max);
			if(min < x && x < max) {
				return x;
			}
		}while(true);
	}
	
	public static Color getRandomColor() {
		return new Color(getRandomInt(255), getRandomInt(255), getRandomInt(255));
	}
	
	/**
	 * @description	： 获得一个随机点
	 * @param maxX 最大不超过这个横坐标
	 * @param maxY 最大不超过这个纵坐标
	 * @param minDistance 这个点距离横轴和纵轴的最小距离
	 * @return
	 */
	public static Position createRandomPosition(int maxX,int maxY,int minDistance) {
		
		if(maxX < 0 || maxY < 0 || minDistance < 0) {
			throw new IllegalArgumentException("所有参数都要大于0");
		}
		if(maxX <= minDistance || maxY <= minDistance) {
			throw new IllegalArgumentException("必须满足条件 maxX > minDistance && maxY > minDistance");
		}
		
		int minLocationX = 0 + minDistance;
		int maxLocationX = maxX - minDistance;
		
		int minLocationY = 0 + minDistance;
		int maxLocationY = maxY - minDistance;
		
		int x = 0;
		do {
			x = getRandomInt(maxLocationX);
			if(minLocationX <= x && x <= maxLocationX) {
				break;
			}
		}while(true);
		
		int y = 0;
		do {
			y = getRandomInt(maxLocationY);
			if(minLocationY <= y && y <= maxLocationY) {
				break;
			}
		}while(true);
		
		return new Position(x, y);
	}

	public static ImageAreaType[][] getSelectBorder(ImageWrapper backgroundImageWrapper) {
		
		ImageAreaType[][] area = backgroundImageWrapper.getMapping();
		
		ImageAreaType[][] border = new ImageAreaType[area.length][area[0].length];
		initImageAreaType(border);
		
		//从上往下扫描
		for(int x =0; x < area.length;x++) {
			
			Position borderStart = null;
			Position lastPosition = null;
			for(int y = 0;y< area[x].length;y++) {
				
				if(borderStart == null) {
					//第一个遇到的选择的，那么就是边界
					if(area[x][y] == ImageAreaType.SELECT) {
						borderStart = new Position(x, y);
					}
				}
				
				if(borderStart != null && area[x][y] == ImageAreaType.UNSELECT) {
					break;
				}else{
					lastPosition = new Position(x,y);
				}
			}
			if(borderStart != null) {
				border[borderStart.x][borderStart.y] = ImageAreaType.SELECT;
			}
			if(lastPosition != null) {
				border[lastPosition.x][lastPosition.y] = ImageAreaType.SELECT;
			}
		}
		return border;
	}
}
