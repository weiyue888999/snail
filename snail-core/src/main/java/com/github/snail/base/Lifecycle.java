package com.github.snail.base;

/**
 * @author 		：weiguangyue
 * 
 * 具有生命周期start和stop方法的服务对象-->启停服务
 */
public interface Lifecycle {

	/**
	 * @description	： 启动服务
	 */
	void start();
	
	/**
	 * @description	： 是否已经启动
	 */
	boolean isRunning();
	
	/**
	 * @description	： 停止服务
	 */
	void stop();
	
	/**
	 * @description	： 是否已经停止
	 */
	boolean isStoped();
}
