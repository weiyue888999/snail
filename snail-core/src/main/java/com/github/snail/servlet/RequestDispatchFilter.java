package com.github.snail.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Constants;
import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.TempFileService;
import com.github.snail.core.impl.DefaultTempFileServiceImpl;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;
import com.github.snail.logging.MDC;

/**
  @author 		：weiguangyue
  
  分发请求Filter
 
 */
public class RequestDispatchFilter extends AbstractBaseFilter{
	
	private static final Log log = LogFactory.getLog(RequestDispatchFilter.class);
	
	private String instanceId;
	
	private CaptchaController captchaController;
	
	private TempFileService tempFileService;
	
	private final Map<String,Filter> mapping = new HashMap<String,Filter>(5);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		super.init(filterConfig);
		
		this.initCaptchaController(filterConfig);

		this.initTempateService(filterConfig);
		
		this.initCommonResourceLoader(filterConfig);
		
		this.initInnerImageRepository(filterConfig);
		
		this.initRequestMapping();
		
		this.initRequestMappingFilters(filterConfig);
		
	}

	//TODO
	private void initInnerImageRepository(FilterConfig filterConfig) {
		
	}

	//TODO
	private void initCommonResourceLoader(FilterConfig filterConfig) {
		
	}

	protected void initCaptchaController(FilterConfig filterConfig) throws ServletException {
		
		//TODO 这里进行手动创建captchaController并分派各个不同类型
		//embedded 嵌入式
		//trigger 触发式
		//popup 弹出式
		String captchaStyle = filterConfig.getInitParameter("captchaStyle");
		if(Utils.isEmpty(captchaStyle)){
			throw new IllegalArgumentException("filter param [captchaStyle] can't be null");
		}
		if(!Constants.CAPTCHA_STYLE_EMBEDDED.equals(captchaStyle) && !Constants.CAPTCHA_STYLE_TRIGGER.equals(captchaStyle) && !Constants.CAPTCHA_STYLE_POPUP.equals(captchaStyle)){
			//填写的style不是预设的
			throw new IllegalArgumentException("filter param [captchaStyle] only should be embedded , trigger or popup");
		}
		
		try {
			//看看是不是3.0以后的容器
			Class.forName("javax.servlet.FilterRegistration");
			
			//找到这个filter在web.xml里面配置的url pattern
			FilterRegistration filterRegistration = filterConfig.getServletContext().getFilterRegistration(filterConfig.getFilterName());
			Collection<String> mappings = filterRegistration.getUrlPatternMappings();
			if(mappings.isEmpty()) {
				throw new IllegalStateException("can't find one url pattern mapping for filter[filterName="+ filterConfig.getFilterName() +",class="+ this.getClass().getName() +"]");
			}else {
				String mapping = mappings.iterator().next();
				
				this.instanceId = filterConfig.getFilterName();
				this.captchaController = new CaptchaController(this.instanceId,captchaStyle,mapping,filterConfig);
			}
		} catch (ClassNotFoundException e) {
			//不是3.0的容器，我们看过滤器初始化参数是否携带instanceId和filterUrlPatternMapping
			log.warn("can't find the class[javax.servlet.FilterRegistration],init instanceId and filterUrlPatternMapping in FilterConfig");
			
			this.instanceId = filterConfig.getInitParameter("instanceId");
			if(Utils.isEmpty(this.instanceId)) {
				throw new IllegalStateException("can't find initParameter[filterUrlPatternMapping] for filter[filterName="+ filterConfig.getFilterName() +",class="+ this.getClass().getName() +"] in web.xml");
			}
			
			String mapping = filterConfig.getInitParameter("filterUrlPatternMapping");
			if(Utils.isEmpty(mapping)) {
				throw new IllegalStateException("can't find initParameter[filterUrlPatternMapping] for filter[filterName="+ filterConfig.getFilterName() +",class="+ this.getClass().getName() +"] in web.xml");
			}
			this.captchaController = new CaptchaController(this.instanceId,captchaStyle,mapping,filterConfig);
		}

		this.captchaController.start();
	}

	/**
	 * 线程池资源可贵，所以在这里只创建一个共享的TempFileService，多个instance进行共享
	 * 一般由在web.xml中排在第一位的RequestDispatchFilter进行初始化和start
	 * 由在web.xml中排在最后一位的RequestDispatchFilter进行销毁和stop
	 * 
	 * 所以，在配置这个的时候，出第一个RequestDispatchFilter中关于tempFileKeepAliveMinutes和cleanerScheduleIntervalSeconds之外的其他RequestDispatchFilter
	 * 的配置都是无效的!!!
	 * 
	 * @param filterConfig
	 */
	private void initTempateService(FilterConfig filterConfig) {
		this.tempFileService = (TempFileService) filterConfig.getServletContext().getAttribute(TempFileService.class.getName());
		if(this.tempFileService == null){
			long tempFileKeepAliveMinutes = 1L;
			long cleanerScheduleIntervalSeconds = 30L;
			{
				String val = filterConfig.getInitParameter("tempFileKeepAliveMinutes");
				tempFileKeepAliveMinutes = Utils.isNotEmpty(val) ? Utils.parsePositiveLong(val, "tempFileKeepAliveMinutes") : tempFileKeepAliveMinutes;
			}
			{
				String val = filterConfig.getInitParameter("cleanerScheduleIntervalSeconds");
				cleanerScheduleIntervalSeconds = Utils.isNotEmpty(val) ? Utils.parsePositiveLong(val, "cleanerScheduleIntervalSeconds") : cleanerScheduleIntervalSeconds;
			}
			this.tempFileService = new DefaultTempFileServiceImpl(tempFileKeepAliveMinutes,cleanerScheduleIntervalSeconds);
			this.tempFileService.start();
			
			log.info(this.getClass().getSimpleName()+"[instanceId="+ instanceId +"]" + " create tempFileService["+ System.identityHashCode(tempFileService) +"]");
			
			filterConfig.getServletContext().setAttribute(TempFileService.class.getName(),this.tempFileService);
		}else {
			log.info(this.getClass().getSimpleName()+"[instanceId="+ instanceId +"]" + " use created tempFileService["+ System.identityHashCode(tempFileService) +"]");
		}
		this.tempFileService.increaseRefCount();
	}
	
	/**
	 * @description	： 初始化各类内部Filter
	 */
	private void initRequestMapping() throws ServletException{
		
		//js资源和css资源
		this.mapping.put("resource", new InnerResourceRenderFilter( captchaController));
		
		//刷新验证码
		this.mapping.put("refresh", new RefreshFilter(captchaController));
		
		//图片
		this.mapping.put("image",new ImageFilter(captchaController));
		
		//验证验证码
		this.mapping.put("verify",new VerifyFilter(captchaController));
		
		//通知,只是用在popup风格(弹出式)
		this.mapping.put("notify",new NotifyFilter(captchaController));
	}
	
	private void initRequestMappingFilters(FilterConfig filterConfig) throws ServletException {

		for(Entry<String, Filter> e : this.mapping.entrySet()) {
			String type = e.getKey();
			Filter filter = e.getValue();
			log.debug("init mapping filter["+ type+ "-->" + filter+"]");
			filter.init(filterConfig);
		}
	}

	@Override
	public void destroy() {
		
		for(Filter filter : this.mapping.values()) {
			filter.destroy();
		}

		this.tempFileService.decreaseRefCount();
		//引用计数为0需要销毁
		if(this.tempFileService.getRefCount() <= 0){
			this.tempFileService.stop();
			this.getServletContext().removeAttribute(TempFileService.class.getName());
		}

		this.captchaController.stop();
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		MDC.put(Constants.MDC_KEY_INSTANCE_ID, this.instanceId);
		
		if(this.captchaController.isProcessRequestOrdered()) {
			synchronized (this) {
				super.doFilter(request, response, chain);
			}
		}else {
			super.doFilter(request, response, chain);
		}
		
		MDC.remove(Constants.MDC_KEY_INSTANCE_ID);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain chain)throws IOException, ServletException {
		
		ClientVerifyContext context = ClientVerifyContext.build(request,this.captchaController);
		context.increaseRequestCount();
		
		String qs = request.getQueryString();
		String type = request.getParameter("type");
		
		Filter mappedFilter = this.mapping.get(type);
		if(mappedFilter != null) {
			
			log.debug(String.format("QueryString=%s --> %s",type,qs,mappedFilter.getClass().getSimpleName()));
			Map<String, String[]> rm = request.getParameterMap();
			Iterator<Entry<String, String[]>> it = rm.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, String[]> e = it.next();
				String name = e.getKey();
				String[] vs = e.getValue();
				if(vs == null) {
					log.debug("requestParam "+name+"=null");					
				}else if(vs.length == 1) {
					log.debug("requestParam "+name+"="+vs[0]);
				}else {
					log.debug("requestParam "+name+"="+vs);
				}
			}
			mappedFilter.doFilter(request, response, chain);
		}else {
			log.warn(String.format("type mapping %s not found",qs));
			response.setStatus(Constants.STATUS_CODE_404);
		}
	}
	
	@Override
	protected boolean acceptDoFilterInternal(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse, FilterChain chain) {
		return true;
	}

	@Override
	protected List<String> supportMethods() {
		return Arrays.asList(Constants.HTTP_REQUEST_METHOD_POST,Constants.HTTP_REQUEST_METHOD_GET);
	}
}
