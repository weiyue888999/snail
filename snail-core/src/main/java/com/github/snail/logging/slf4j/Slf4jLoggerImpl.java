package com.github.snail.logging.slf4j;

import org.slf4j.Logger;

import com.github.snail.logging.Log;

class Slf4jLoggerImpl implements Log {

	private final Logger log;

	public Slf4jLoggerImpl(Logger logger) {
		log = logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s, e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void trace(String s) {
		log.trace(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

	@Override
	public void info(String s) {
		log.info(s);
	}

	@Override
	public void warn(String s, Throwable e) {
		log.warn(s, e);
	}
}