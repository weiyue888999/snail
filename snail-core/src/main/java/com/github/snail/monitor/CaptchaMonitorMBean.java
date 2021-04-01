package com.github.snail.monitor;

/**
 * @author 		：weiguangyue
 */
public interface CaptchaMonitorMBean {

	long increaseTotalAccessCount();

	void cleanTotalAccessCount();

	long increaseTotalVerifyFailCount();

	void cleanTotalVerifyFailCount();

	long increaseTotalVerifySuccessCount();

	void cleanTotalVerifySuceessCount();

	long increaseTotalAccessCostTime(long time);
	
	void cleanTotalAccessCostTime();
	
	long getTotalAccessCount();

	long getTotalVerifySuccessCount();

	long getTotalVerifyFailCount();
	
	long getTotalAccessCostTime();
	
	void cleanAll();
	
	String dump();
}
