package com.github.snail.graph;

/**
 @author ：weiguangyue
 
矩形

有4个点，如下图所示：

A                         B
###########################
#                         #
#                         #
#                         #
###########################
C                         D




 */
public class Rectangle {

	private final Position a;
	private final Position b;
	private final Position c;
	private final Position d;
	
	public Rectangle(Position a, Position b, Position c, Position d) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Position getA() {
		return a;
	}
	public Position getB() {
		return b;
	}
	public Position getC() {
		return c;
	}
	public Position getD() {
		return d;
	}
	public int getHeight() {
		return c.y - a.y;
	}
	public int getWidth() {
		return b.x - a.x;
	}

	@Override
	public String toString() {
		return "Rectangle [a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + "]";
	}
}
