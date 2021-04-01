package com.github.snail.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证handler
 */
public interface VerifyHandler {

	/**
	 * 验证
	 * @param request
	 * @param response
	 * @return
	 */
	VerifyResult verify(HttpServletRequest request, HttpServletResponse response);
	
}
