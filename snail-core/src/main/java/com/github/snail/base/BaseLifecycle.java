package com.github.snail.base;

/**
 * @author 		：weiguangyue
 * 
 * 基础生命周期
 */
public abstract class BaseLifecycle implements Lifecycle{
	
	private enum Status{
		UNSTARTED,
		RUNNING,
		STOPED
	}
	private Status currentStatus = Status.UNSTARTED;
	
	protected synchronized void checkRunning() {
		if(this.currentStatus != Status.RUNNING) {
			throw new IllegalStateException("status is not running!!!");
		}
	}
	
	@Override
	public final synchronized boolean isRunning() {
		return this.currentStatus == Status.RUNNING;
	}

	@Override
	public final synchronized boolean isStoped() {
		return this.currentStatus == Status.STOPED;
	}

	@Override
	public final synchronized void start() {
		if(this.currentStatus != Status.UNSTARTED){
			return;
		}
		this.doStart();
		this.currentStatus = Status.RUNNING;
	}
	
	@Override
	public final synchronized void stop() {
		if(this.currentStatus != Status.RUNNING) {
			return;
		}
		this.doStop();
		this.currentStatus = Status.STOPED;
	}

	protected abstract void doStart();

	protected abstract void doStop();
}
