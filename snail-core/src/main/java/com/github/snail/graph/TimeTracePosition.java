package com.github.snail.graph;

import java.io.Serializable;

/**
 * @author 		：weiguangyue
 * 
 * 时间跟踪的位置
 * 
 */
public class TimeTracePosition extends Position implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public final long time;
	
	public TimeTracePosition(int x, int y) {
		super(x, y);
		this.time = 0;
	}

	public TimeTracePosition(int x, int y, long time) {
		super(x, y);
		this.time = time;
	}

	@Override
	public String toString() {
		return "TimeTracePosition [time=" + time + ", x=" + x + ", y=" + y + "]";
	}
}
