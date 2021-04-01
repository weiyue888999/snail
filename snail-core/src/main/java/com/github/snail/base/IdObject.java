package com.github.snail.base;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author weiguangyue
 * 
 * id对象,用于唯一标示一个对象
 */
public class IdObject {
	
	private static final long START = 0L/**System.currentTimeMillis()**/;
	
	private static final AtomicLong ID_SEQUENCE = new AtomicLong(START);
	
	private static final long getNextId() {
		return ID_SEQUENCE.incrementAndGet();
	}

	private long id = getNextId();

	/**
	 * @description	： 获得对象id
	 * @return
	 */
	public long getId() {
		return id;
	}
}