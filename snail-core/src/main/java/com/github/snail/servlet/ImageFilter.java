package com.github.snail.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.Verify;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 */
class ImageFilter extends AbstractCaptchaControllerFilter{
	
	private final Log log = LogFactory.getLog(getClass());

	public ImageFilter(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		boolean success = false;
		String id = request.getParameter("id");
		String imtk = request.getParameter("imtk");
		if(id != null && !id.trim().equals("") && imtk != null && !imtk.trim().equals("")) {
			ClientVerifyContext verifyContext = ClientVerifyContext.get(request,getCaptchaController());
			if(verifyContext != null) {
				Verify verify = verifyContext.getVerify();
				if(verify != null) {
					if(verify.getImtk().equals(imtk)) {
						File file = verify.getImageFileById(id);
						if(file.exists()) {
							success = true;
							this.renderFileStream(request, response, "image/png;charset=UTF-8", file);			
						}						
					}
				}
			}
		}
		if(!success) {
			log.warn("download image["+ id +"] not exist !!! mybe someone want study me");
			response.setStatus(Constants.STATUS_CODE_404);
		}
	}

	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_GET);
	}
}
