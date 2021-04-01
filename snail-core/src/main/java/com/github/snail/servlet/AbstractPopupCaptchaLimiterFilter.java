package com.github.snail.servlet;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.ClientVerifyContext.State;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.logging.MDC;

/**
 * @author 		：weiguangyue
 * 
 * 
 * 抽象的弹出式验证码拦截器,继承此类，完成个性化定制
 */
public abstract class AbstractPopupCaptchaLimiterFilter implements Filter{
	
	private static final Log log = LogFactory.getLog(AbstractPopupCaptchaLimiterFilter.class);
	
	private volatile ScheduledExecutorService executor;
	
	private volatile ScheduledFuture<?> futrue;
	
	private int delaySeconds = 2;
	/**
	 * 重试次数
	 */
	private volatile int retryCount = 30;
	/**
	 * 上游过滤器
	 */
	private String upstreamFilterName;
	
	private volatile CaptchaController captchaController;
	
	private String filterName;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.filterName = filterConfig.getFilterName();
		
		this.upstreamFilterName = filterConfig.getInitParameter("upstreamFilterName");
		if(Utils.isEmpty(upstreamFilterName)) {
			throw new IllegalArgumentException("upstreamFilterName can't be null");
		}
		String controllerAttributeName = CaptchaController.class.getName()+"_"+this.upstreamFilterName;
		
		if(filterConfig.getServletContext().getAttribute(controllerAttributeName) == null) {
			log.warn("can't find attribute[ name="+ controllerAttributeName +" ] in servletContext,retry again later");
			
			//FIXME 为何Filter的初始化顺序不是在web.xml中的排列顺序
			//这里使用调度器尝试一定次数的获取
			this.executor = Executors.newSingleThreadScheduledExecutor(new Utils.NamedThreadFactory("fetch-CaptachaController-retry-"+this.filterName+"-"+System.identityHashCode(this)));
			this.futrue = this.executor.schedule(new RetryTask(filterConfig.getServletContext(),controllerAttributeName), delaySeconds, TimeUnit.SECONDS);
			
		}else {
			this.captchaController = (CaptchaController) filterConfig.getServletContext().getAttribute(controllerAttributeName);
		}
	}
	
	private class RetryTask implements Runnable{
		
		private ServletContext servletContext;
		private String controllerAttributeName;
		
		public RetryTask(ServletContext servletContext,String controllerAttributeName) {
			super();
			this.servletContext = servletContext;
			this.controllerAttributeName = controllerAttributeName;
		}

		@Override
		public void run() {
			
			if(servletContext.getAttribute(controllerAttributeName) == null) {
				retryCount --;
				if(retryCount > 0) {
					futrue = executor.schedule(this, delaySeconds, TimeUnit.SECONDS);
				}else {
					//
					log.error("can't find attribute[ name="+ controllerAttributeName +" ] in servletContext,please check web.xml !!!");
					
					shutdownExecutor();
				}
			}else {
				//find it
				
				log.info("find attribute[ name="+ controllerAttributeName +" ] in servletContext,retryCount="+retryCount);
				
				captchaController = (CaptchaController) servletContext.getAttribute(controllerAttributeName);
				
				shutdownExecutor();
			}
		}
	}
	
	private synchronized void shutdownExecutor() {

		if(this.futrue != null) {
			this.futrue.cancel(true);
			
			//help gc
			this.futrue = null;
		}
		if(this.executor != null) {
			Utils.ExecutorUtil.shutdownNow(this.executor, 1000*delaySeconds);
			
			//help gc
			this.executor = null;
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		MDC.put(Constants.MDC_KEY_SESSION_ID,httpServletRequest.getSession().getId());
		
		
		if(this.captchaController == null) {
			String controllerAttributeName = CaptchaController.class.getName()+"_"+this.upstreamFilterName;
			throw new IllegalStateException("find attribute[ name="+ controllerAttributeName +" ] in servletContext");
		}
		
		MDC.put(Constants.MDC_KEY_INSTANCE_ID, this.captchaController.getInstanceId());
		
		if(accept(httpServletRequest,httpServletResponse)) {
			//这里必须这样
			chain.doFilter(httpServletRequest, httpServletResponse);
		}else {
			this.doUnaccept(httpServletRequest,httpServletResponse,chain);
		}
		
		MDC.remove(Constants.MDC_KEY_INSTANCE_ID);
		
		MDC.remove(Constants.MDC_KEY_SESSION_ID);
	}

	protected boolean accept(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ClientVerifyContext context = ClientVerifyContext.get(httpServletRequest,this.captchaController);
		return context != null && context.getState() == State.NOTIFIED;
	}

	protected abstract void doUnaccept(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,FilterChain chain) throws IOException, ServletException;
	
	@Override
	public void destroy() {
		shutdownExecutor();
	}
}
