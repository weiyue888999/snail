package com.github.snail.decoder.impl;

import com.github.snail.decoder.TracePositionTraceDecoder;

public class NoopOperatorTracePositionTraceDecoder implements TracePositionTraceDecoder{

	@Override
	public String getName() {
		return "noop";
	}

	@Override
	public String decode(String encryptedStr) {
		return encryptedStr;
	}

	@Override
	public String javascriptEncodeCode() {
		return "function ef(str){return str;}";
	}
}
