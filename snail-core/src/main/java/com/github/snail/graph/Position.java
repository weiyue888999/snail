package com.github.snail.graph;

import java.io.Serializable;

/**
@author weiguangyue
 
 坐标位置
 
#################################### 这个是横坐标，越往右，这个值就越大，就是X越大
# 
#
#
#        . 这个点的位置就是(x,y),也就是这个对象要表示的意思
#
#
#
#
#
#
# 
#
#


这个是纵坐标，越往下，这个值就越大，就是Y越大

**/
public class Position implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public final int x;
	public final int y;
	
	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
