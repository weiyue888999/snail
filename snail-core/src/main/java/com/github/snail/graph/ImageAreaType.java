package com.github.snail.graph;

public enum ImageAreaType {

	UNSELECT(0),/*不选择的区域*/
	SELECT(1);/*选择的区域*/;
	
	private int type;
	
	ImageAreaType(int type) {
		this.type = type;
	}
}
