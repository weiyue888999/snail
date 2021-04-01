package com.github.snail.monitor;

import java.util.concurrent.atomic.AtomicLong;

import com.github.snail.base.BaseLifecycleMBean;

/**
 * @author 		：weiguangyue
 */
public class CaptchaMonitor extends BaseLifecycleMBean implements CaptchaMonitorMBean{

	//-------统计信息--------------------------------------------------------------------------------start
	/**
	 * 总访问次数
	 */
	private final AtomicLong totalAccessCount = new AtomicLong(0L);
	/**
	 * 总验证成功个数
	 */
	private final AtomicLong totalVerifySuccessCount = new AtomicLong(0L);
	/**
	 * 总验证失败个数
	 */
	private final AtomicLong totalVerifyFailCount = new AtomicLong(0L);
	/**
	 * 总访问消耗时间
	 */
	private final AtomicLong totalAccessCostTime = new AtomicLong(0L);
	//-------统计信息--------------------------------------------------------------------------------end
	
	@Override
	public long increaseTotalAccessCostTime(long time) {
		return this.totalAccessCostTime.getAndAdd(time);
	}

	@Override
	public void cleanTotalAccessCostTime() {
		this.totalAccessCostTime.set(0);
	}

	@Override
	public long increaseTotalAccessCount() {
		return this.totalAccessCount.incrementAndGet();
	}

	@Override
	public void cleanTotalAccessCount() {
		this.totalAccessCount.set(0);
	}

	@Override
	public long increaseTotalVerifyFailCount() {
		return this.totalVerifyFailCount.incrementAndGet();
	}

	@Override
	public void cleanTotalVerifyFailCount() {
		this.totalVerifyFailCount.set(0);
	}

	@Override
	public long increaseTotalVerifySuccessCount() {
		return this.totalVerifySuccessCount.incrementAndGet();
	}

	@Override
	public void cleanTotalVerifySuceessCount() {
		this.totalVerifySuccessCount.set(0);
	}
	
	@Override
	public long getTotalAccessCount() {
		return totalAccessCount.longValue();
	}

	@Override
	public long getTotalVerifySuccessCount() {
		return totalVerifySuccessCount.longValue();
	}
	
	@Override
	public long getTotalVerifyFailCount() {
		return totalVerifyFailCount.longValue();
	}
	
	@Override
	public long getTotalAccessCostTime() {
		return totalAccessCostTime.longValue();
	}

	@Override
	public void cleanAll() {
		this.totalAccessCostTime.set(0L);
		this.totalAccessCount.set(0L);
		this.totalVerifyFailCount.set(0L);
		this.totalVerifySuccessCount.set(0L);
	}

	@Override
	public String dump() {
		return toString();
	}

	@Override
	public String toString() {
		return "CaptchaMonitor [totalAccessCount=" + totalAccessCount + ", totalVerifySuccessCount="
				+ totalVerifySuccessCount + ", totalVerifyFailCount=" + totalVerifyFailCount + ", totalAccessCostTime="
				+ totalAccessCostTime + "]";
	}
}
