package com.github.snail.core.impl;

/**
 * @author 		：weiguangyue
 * 
 * 共享对象,多个对象共享这类对象,有第一个对象要使用的对象创建,由最后一个要销毁的对象销毁
 */
public interface Sharedable {

	/**
	 * 增加引用计数
	 */
    void increaseRefCount();

    /**
     * 减小引用计数
     */
    void decreaseRefCount();

    /**
     * 获得引用计数
     * @return
     */
    int getRefCount();
    
    /**
     * 获得创建时间
     * @return
     */
    long getCreateTime();
}
