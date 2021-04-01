package com.github.snail.core.impl;

import com.github.snail.base.BaseLifecycle;
import com.github.snail.core.Verify;
import com.github.snail.core.VerifyFactory;
import com.github.snail.exceptions.VerifyException;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

public class TimeVerifyFactory extends BaseLifecycle implements VerifyFactory{
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private VerifyFactory delegate;
	
	public TimeVerifyFactory(VerifyFactory delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Verify create() throws VerifyException {
		
		long start = System.currentTimeMillis();
		
		Verify ret = this.delegate.create();
		
		long end = System.currentTimeMillis();
		long cost = end - start;
		
		log.debug("create verify cost "+ cost + " ms");
		
		return ret;
	}

	@Override
	protected void doStart() {
		this.delegate.start();
	}

	@Override
	protected void doStop() {
		this.delegate.stop();
	}
}
