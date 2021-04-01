package com.github.snail.core.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.snail.common.Utils;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author weiguangyue
 */
public final class DefaultTempFileServiceImpl extends AutoCleanableTempFileService{
	
	private static final Log log = LogFactory.getLog(DefaultTempFileServiceImpl.class);
	
	private long createTime = System.currentTimeMillis();

	private final AtomicInteger refCount = new AtomicInteger(0);
	
	private final ThreadPoolExecutor cleanerExecutor = new ThreadPoolExecutor(//
			2,//
			2, //
			60L,//
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(1024),//
			new Utils.NamedThreadFactory("captcha_tempfile_cleaner",true)
		);//
	
	public DefaultTempFileServiceImpl(long tempFileKeepAliveMinutes,long cleanerScheduleIntervalSeconds) {
		super(tempFileKeepAliveMinutes,cleanerScheduleIntervalSeconds);
	}
	
	@Override
	public void doStop() {
		super.doStop();
		Utils.ExecutorUtil.gracefulShutdown(this.cleanerExecutor, 1000);
	}
	
	@Override
	public synchronized File get(String name) {
		File root = this.getTempRootDir();
		if(!root.exists()) {
			root.mkdir();
		}
		File tempFile = new File(root,name);
		return tempFile;
	}
	
	@Override
	public synchronized File create(String name) {
		File root = this.getTempRootDir();
		if(!root.exists()) {
			root.mkdir();
		}
		File tempFile = new File(root,name);
		try {
			tempFile.createNewFile();
		} catch (IOException e) {
			log.error("create temp file error",e);
		}
		return tempFile;
	}

	@Override
	public void delete(final File f) {
		
		if(!isRunning()) {
			return;
		}
		if(!f.exists() || f.isDirectory()) {
			return;			
		}
		
		final Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					if(f.exists() && f.isFile()) {
						f.delete();
					}
				}catch (Throwable e) {
					log.error(String.format("delete temp file %s error",f),e);
				}
			}
		};
		this.cleanerExecutor.submit(task);
	}
	
	@Override
	public void delete(final File ...fs) {
		
		if(!isRunning()) {
			return;
		}
		for(File f : fs) {
			if(!f.exists() || f.isDirectory()) {
				return;						
			}
		}
		
		final Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(File f : fs) {
					if(f.exists() && f.isFile()) {
						try {
							f.delete();
						}catch (Throwable e) {
							log.error(String.format("delete temp file %s error",f),e);
						}
					}
				}
			}
		};
		this.cleanerExecutor.submit(task);
	}

	@Override
	public void increaseRefCount() {
		int result = this.refCount.incrementAndGet();
		log.debug("incressRefCount to "+result);
	}

	@Override
	public void decreaseRefCount() {
		int result = this.refCount.decrementAndGet();
		log.debug("decreaseRefCount to "+result);
	}

	@Override
	public int getRefCount() {
		return this.refCount.get();
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}
}
