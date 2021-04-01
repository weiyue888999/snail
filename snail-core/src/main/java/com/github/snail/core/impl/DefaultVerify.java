package com.github.snail.core.impl;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.github.snail.common.Utils;
import com.github.snail.core.Verify;

class DefaultVerify implements Verify , Serializable{

	private static final long serialVersionUID = 1030420263530970939L;
	/**
	 * 进程id
	 */
	private final int pid = Utils.currentPid();
	/**
	 * 上次引用的时间
	 */
	private final AtomicLong verifyCount = new AtomicLong();
	/**
	 * 上次引用的时间
	 */
	private final AtomicLong lastRefTime = new AtomicLong(System.currentTimeMillis());
	/**
	 * 引用计数
	 */
	private final AtomicLong refCount = new AtomicLong(1);
	/**
	 * 创建时间
	 */
	private final Date ct = new Date();
	/**
	 * token
	 */
	private final String imtk = UUID.randomUUID().toString();
	
	private final Map<String,File> imageMap = new HashMap<String,File>(2);
	
	private String captchaControllerInstanceId;
	
	/**
	 * 源图片id
	 */
	private final String siId;
	
	private final File si;
	
	/**
	 * 滑块图片id
	 */
	private final String miId;
	
	private final File mi;
	/**
	 * 坐标x
	 */
	private final int x;
	/**
	 * 坐标Y
	 */
	private final int y;
	
	public DefaultVerify(String captchaControllerInstanceId,String siId,File si,String miId,File mi, int x, int y) {
		super();
		this.captchaControllerInstanceId = captchaControllerInstanceId;
		this.siId = siId;
		this.si = si;
		this.miId = miId;
		this.mi = mi;
		this.x = x;
		this.y = y;
		this.imageMap.put(siId, si);
		this.imageMap.put(miId, mi);
	}
	
	@Override
	public File getImageFileById(String id) {
		return this.imageMap.get(id);
	}

	@Override
	public Date getCt() {
		return ct;
	}

	@Override
	public String getImtk() {
		return imtk;
	}

	@Override
	public File getSi() {
		return si;
	}

	@Override
	public File getMi() {
		return mi;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public long getLastRefTime() {
		return this.lastRefTime.get();
	}
	
	public void refreshLastRefTime() {
		this.lastRefTime.set(System.currentTimeMillis());
	}
	
	@Override
	public void increaseVerifyCount() {
		this.verifyCount.incrementAndGet();
	}
	
	@Override
	public long getVerifyCount() {
		return this.verifyCount.get();
	}
	
	public long getRefCount() {
		return this.refCount.get();
	}
	
	public long increaseRefCount() {
		return this.refCount.incrementAndGet();
	}
	
	public long decreaseRefCount() {
		return this.refCount.decrementAndGet();
	}

	public int getPid() {
		return pid;
	}
	
	public String getCaptchaControllerInstanceId() {
		return captchaControllerInstanceId;
	}

	public void setCaptchaControllerInstanceId(String captchaControllerInstanceId) {
		this.captchaControllerInstanceId = captchaControllerInstanceId;
	}

	@Override
	public String toString() {
		return "DefaultVerify [pid=" + pid + ", verifyCount=" + verifyCount + ", lastRefTime=" + lastRefTime
				+ ", refCount=" + refCount + ", ct=" + ct + ", imtk=" + imtk + ", imageMap=" + imageMap
				+ ", captchaControllerInstanceId=" + captchaControllerInstanceId + ", siId=" + siId + ", si=" + si
				+ ", miId=" + miId + ", mi=" + mi + ", x=" + x + ", y=" + y + "]";
	}

}
