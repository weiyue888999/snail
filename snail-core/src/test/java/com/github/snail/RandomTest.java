package com.github.snail;

import com.github.snail.graph.GraphUtils;
import com.github.snail.graph.Position;

public class RandomTest {

	//@org.junit.Test
	public void testOne() {
		doCreateRandomPosition();			
	}
	
	//@org.junit.Test
	public void test() {
		for(int i=0;i<20;i++) {
			doCreateRandomPosition();			
		}
	}

	private void doCreateRandomPosition() {
		Position p = GraphUtils.createRandomPosition(338,190,50);
		System.out.println(p);
	}
}
