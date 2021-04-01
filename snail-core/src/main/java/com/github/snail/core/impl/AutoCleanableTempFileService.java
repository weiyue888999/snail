package com.github.snail.core.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.snail.base.BaseLifecycle;
import com.github.snail.common.Utils;
import com.github.snail.core.TempFileService;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;


/**
 @author weiguangyue
 
 the template code of active object pattern
 
 */
abstract class AutoCleanableTempFileService extends BaseLifecycle implements TempFileService{

	private static final Log log = LogFactory.getLog(AutoCleanableTempFileService.class);
	
	private long tempFileKeepAliveMinutes = 1L;

	private long tempFileKeepAliveTime =  TimeUnit.MINUTES.toMillis(tempFileKeepAliveMinutes);

	private final File baseDir = Utils.getUserDirectory();

	private final String tempFileRootDirName = "snail_captcha_temp";
	
	//1分钟
	private long cleanerScheduleIntervalSeconds =  60L*1;

	private final ScheduledExecutorService cleanerScheduledExecutor  = Executors.newScheduledThreadPool(1, new Utils.NamedThreadFactory("zfcaptcha_schedule_cleaner",true));
	
	private ScheduledFuture<?> scheduledFuture;
	
	private File tempFileRootDir;

	private final String cleanLockFileName = "cleaner.lock";

	private File mutileJvmLockFile;
	
	public AutoCleanableTempFileService(long tempFileKeepAliveMinutes,long cleanerScheduleIntervalSeconds) {
		super();
		this.tempFileKeepAliveMinutes = tempFileKeepAliveMinutes;
		this.tempFileKeepAliveTime = TimeUnit.MINUTES.toMillis(this.tempFileKeepAliveMinutes);
		this.cleanerScheduleIntervalSeconds = cleanerScheduleIntervalSeconds;
	}

	@Override
	public void doStart() {
		try {
			this.doInitStart();
		} catch (Throwable t) {
			log.error("init temp dir "+ this.tempFileRootDir +" error ",t);
		}
	}

	private void doInitStart() {
		
		this.tempFileRootDir = new File(baseDir, tempFileRootDirName);
		if (!this.tempFileRootDir.exists()) {
			this.tempFileRootDir.mkdirs();
		}
		log.info(String.format("init tempFileService,use temp dir:%s,cleanerScheduleIntervalSeconds:%s,tempFileKeepAliveMinutes:%s", this.tempFileRootDir,this.cleanerScheduleIntervalSeconds,this.tempFileKeepAliveMinutes));
		
		this.mutileJvmLockFile = new File(this.tempFileRootDir,cleanLockFileName);
		if(!mutileJvmLockFile.exists()) {
			try {
				mutileJvmLockFile.createNewFile();
			} catch (IOException e) {
				//两个进程同时创建同一个名称的文件，可能会发生异常
				log.error("",e);
			}
		}
		this.scheduledFuture = this.cleanerScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				
				clean();
			}
		}, cleanerScheduleIntervalSeconds, cleanerScheduleIntervalSeconds, TimeUnit.SECONDS);
	}
	
	@Override
	public void doStop() {
		try {
			this.scheduledFuture.cancel(true);
		}catch (Throwable e) {
			log.error("",e);
		}
		Utils.ExecutorUtil.gracefulShutdown(this.cleanerScheduledExecutor, 5000);
	}

	protected void clean() {
		
		if(!isRunning()) {
			return;
		}
		
		RandomAccessFile cleanLockFile = null;
		FileChannel channel = null;
		FileLock lock = null;
		try {
			cleanLockFile = new RandomAccessFile(this.mutileJvmLockFile, "rw");
			
			channel = cleanLockFile.getChannel();
			lock = channel.tryLock();
			
			if(lock != null) {
				try {
					int cleanCount = this.doClean(this.tempFileRootDir);
					if(cleanCount > 0) {
						log.debug(String.format("clean tempFile size %s in tempDir %s",cleanCount,this.tempFileRootDir));
					}
				} catch (Throwable t) {
					log.error(String.format("clean temp dir %s error",this.tempFileRootDir),t);
				}
			}else {
				//执行到这里，说明其他的webapp实例已经在做这个动作了,我们也就不必做了,冲突避让
			}
		} catch (Throwable e) {
			log.error("",e);
		}finally {
			if(lock != null) {
				try {
					lock.release();
				} catch (Throwable ex) {
					log.error("",ex);
				}
			}
			if(channel != null) {
				try {
					channel.close();
				} catch (Throwable ex) {
					log.error("",ex);
				}
			}
			if(cleanLockFile != null) {
				try {
					cleanLockFile.close();					
				}catch (Throwable ex) {
					log.error("",ex);
				}
			}
		}
	}

	private int doClean(File dir) {
		File[] files = dir.listFiles();
		if(files == null || files.length <= 0) {
			return 0;
		}
		
		int cleanCount = 0;
		long now = System.currentTimeMillis();
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile() && f.exists() && !f.isHidden()) {
				long lastModefied = f.lastModified();
				boolean needDelete = now - lastModefied > tempFileKeepAliveTime;
				if (needDelete) {
					log.trace(String.format("delete tempFile:%s",f));
					boolean success = f.delete();
					if(success) {
						cleanCount ++;							
					}
				}
			}
		}
		return cleanCount;
	}
	
	protected synchronized File getTempRootDir() {
		this.checkRunning();
		return this.tempFileRootDir;
	}
}
