package com.github.snail.support.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.servlet.RequestDispatchFilter;

/**
 * @author 		：weiguangyue
 * 
 * 类路径下配置文件初始化RequestDispatchFilter,方便产品部门进行快速修改配置
 * 
 */
public class ClasspathConfigProxyRequestDispatchFilter implements Filter{
	
	private final Log log = LogFactory.getLog(getClass());
	/**
	 * 前缀
	 */
	private String prefix = "com.github.snail";
	/**
	 * 配置文件名称
	 */
	private String configFileName = "snail.properties";
	
	private final Filter delegate = new RequestDispatchFilter();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		{
			String val = filterConfig.getInitParameter("configFileName");
			this.configFileName = val != null ? val : this.configFileName;
		}
		
		{
			String val = filterConfig.getInitParameter("prefix");
			this.prefix = val != null ? val : this.prefix;
		}
		
		Map<String,String> params = new HashMap<String,String>();
		
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFileName);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			log.info("load configFile["+ configFileName +"] success!!!");
		} catch (IOException e) {
			throw new ServletException("load configFile :"+ configFileName +" error,cause : "+ e.getMessage(), e);
		}
		
		String prefixInFile = this.prefix + ".";
		
		Set<Entry<Object, Object>> set = properties.entrySet();
		for(Entry<Object, Object> e : set) {
			Object ko = e.getKey();
			Object vo = e.getValue();
			if(ko instanceof String && vo instanceof String) {
				String k = (String)ko;
				String v = (String)vo;
				
				//TODO 防御prefix和value是相同的字符串
				String newK = k.replace(prefixInFile, "");
				
				log.info(newK+"="+v);
				
				params.put(newK, v);
			}else {
				//can't happen
				log.warn("");
			}
		}
		
		this.delegate.init(new DelegateFilterConfig(filterConfig,params));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.delegate.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		this.delegate.destroy();
	}
	
	/**
	 * @author 		：weiguangyue-1571
	 * 
	 * 委托FilterConfig
	 * 
	 */
	private static final class DelegateFilterConfig implements FilterConfig{
		
		private FilterConfig delegateFilterConfig;
		private Map<String,String> params = Collections.emptyMap(); 
	
		public DelegateFilterConfig(FilterConfig delegateFilterConfig,Map<String,String> params) {
			super();
			this.delegateFilterConfig = delegateFilterConfig;
			this.params = params;
		}

		@Override
		public String getFilterName() {
			return this.delegateFilterConfig.getFilterName();
		}

		@Override
		public ServletContext getServletContext() {
			return this.delegateFilterConfig.getServletContext();
		}

		@Override
		public String getInitParameter(String name) {
			return this.params.get(name);
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			Iterator<String> it = this.params.keySet().iterator();
			return new IteratorEnumeration(it);
		}
	}
	
	private static final class IteratorEnumeration implements Enumeration<String>{
		
		private final Iterator<String> it;
		
		public IteratorEnumeration(Iterator<String> it) {
			this.it = it;
		}
		
		@Override
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		@Override
		public String nextElement() {
			return it.next();
		}
	}
}
