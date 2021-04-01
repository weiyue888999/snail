package com.github.snail.exceptions;

/**
 * @author ：weiguangyue
 * 
 * 验证异常
 * 
 */
public class VerifyException extends Exception {

	private static final long serialVersionUID = 1L;

	public VerifyException() {
		super();
	}

	public VerifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public VerifyException(String message) {
		super(message);
	}

	public VerifyException(Throwable cause) {
		super(cause);
	}
}
