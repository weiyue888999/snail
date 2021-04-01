package com.github.snail.core;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 @author 		：weiguangyue
 
 验证上下文

**/
public class ClientVerifyContext implements Serializable{
	
	private static final long serialVersionUID = 2381967027078427588L;

	public static enum State{
		NOT_VERIFY("not_verify"),//没有验证
		VERIFIED("verified"),//验证成功
		NOTIFIED("notified");//已通知
		
		private String tag;
		
		State(String tag){
			this.tag = tag;
		}
		
		public String tag() {
			return this.tag;
		}
	}
	
	private String rtk = UUID.randomUUID().toString();
	
	private transient HttpSession httpSession;
	
	private Date createTime = new Date();
	/**
	 * 总请求次数
	 */
	private AtomicLong requestCount = new AtomicLong(0L);
	/**
	 * 刷新次数
	 */
	private AtomicLong refreshCount = new AtomicLong(0L);
	/**
	 * 失败次数
	 */
	private AtomicLong failCount = new AtomicLong(0L);
	
	private long lastRefreshTime = System.currentTimeMillis();
	
	private State state = State.NOT_VERIFY;
	
	private Verify verify;
	
	private String captchaControllerInstanceId;

	public ClientVerifyContext(HttpSession httpSession,String captchaControllerInstanceId) {
		super();
		this.httpSession = httpSession;
		this.captchaControllerInstanceId = captchaControllerInstanceId;
	}
	
	public void release() {
		this.httpSession.removeAttribute(getAttributeName(this.captchaControllerInstanceId));
	}

	public String getRtk() {
		return rtk;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void cleanRefreshCount() {
		this.refreshCount.set(0L);
	}

	public long increaseRefreshCount() {
		return this.refreshCount.incrementAndGet();
	}
	
	public long getRefreshCount() {
		return this.refreshCount.get();
	}
	
	public long increaseRequestCount() {
		return this.requestCount.incrementAndGet();
	}
	
	public void cleanRequestCount() {
		this.requestCount.set(0L);
	}

	public void cleanFailCount() {
		this.failCount.set(0L);
	}

	public void increaseFailCount() {
		this.failCount.incrementAndGet();
	}

	public Verify getVerify() {
		return verify;
	}

	public void refreshVerify(Verify verify) {
		this.refreshCount.incrementAndGet();
		this.verify = verify;
	}
	
	public void refreshLastRefreshTime() {
		this.lastRefreshTime = System.currentTimeMillis();
	}
	
	public long getLastRefershTime() {
		return this.lastRefreshTime;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	private static String getAttributeName(String captchaControllerInstanceId) {
		return ClientVerifyContext.class.getName()+"_"+ captchaControllerInstanceId;
	}
	
	private static String getAttributeName(CaptchaController captchaController) {
		return ClientVerifyContext.class.getName()+"_"+ captchaController.getInstanceId();
	}

	/**
	 * 构建上下文
	 * @param request
	 * @param captchaController
	 * @return
	 */
	public static ClientVerifyContext build(HttpServletRequest request,CaptchaController captchaController) {
		
		String attributeName = getAttributeName(captchaController);
		
		ClientVerifyContext verifyContext = (ClientVerifyContext)request.getSession().getAttribute(attributeName);
		
		if(verifyContext == null) {
			verifyContext = new ClientVerifyContext(request.getSession(),captchaController.getInstanceId());
			request.getSession().setAttribute(attributeName, verifyContext);
		}
		return verifyContext;
	}
	
	/**
	 * 获得上下文
	 * @param request
	 * @param captchaController
	 * @return
	 */
	public static ClientVerifyContext get(HttpServletRequest request,CaptchaController captchaController) {
		
		String attributeName = getAttributeName(captchaController);
		
		ClientVerifyContext verifyContext = (ClientVerifyContext)request.getSession().getAttribute(attributeName);
		return verifyContext;
	}
}
