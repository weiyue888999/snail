package com.github.snail.verify.impl;

import com.github.snail.verify.VerifyResult;

/**
 * 默认验证结果
 */
public class DefaultVerifyResult implements VerifyResult{
	
	private boolean verifySuccess = false;
	
	private String failReason = "";
	
	public DefaultVerifyResult(boolean verifySuccess) {
		this.verifySuccess = verifySuccess;
	}

	@Override
	public boolean success() {
		return this.verifySuccess;
	}

	@Override
	public String getFailReason() {
		return this.failReason;
	}

	public void setVerifySuccess(boolean verifySuccess) {
		this.verifySuccess = verifySuccess;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
	public static final VerifyResult SuccessResult = new DefaultVerifyResult(true);
	
	public static final VerifyResult FailResult = new DefaultVerifyResult(false);
}
