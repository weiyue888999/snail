package com.github.snail.demoServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.json.JSONWriter;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 * 
 * 当开发国际化功能的时候，可以在浏览器输入 http://127.0.0.1:9999/snail-demo-web/changeLanguage?language=en_US
 * 
 * 模拟把宿主系统的国际化值改成英文
 * 
 */
public class ChangeLanguageDemoServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(ChangeLanguageDemoServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Map<String,Object> resultMap = new HashMap<String,Object>(1);
		resultMap.put("status", "success");
		
		response.setContentType(Constants.CONTENT_TYPE_JSON);
		
		JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.writeMap(resultMap);
		
		String jsonString = jsonWriter.toString();
		log.debug("session "+ request.getSession().getId() +" rende application/json --> "+jsonString);
		
		PrintWriter printWriter = response.getWriter();
		printWriter.write(jsonString);
	}
}
