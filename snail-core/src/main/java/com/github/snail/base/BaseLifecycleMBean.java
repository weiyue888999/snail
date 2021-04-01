package com.github.snail.base;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.github.snail.core.CaptchaController;
import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;

/**
 * @author 		：weiguangyue
 */
public class BaseLifecycleMBean extends BaseLifecycle{
	
	private static final Log log = LogFactory.getLog(CaptchaController.class);

	@Override
	protected void doStart() {
		this.registerMBean();
	}

	@Override
	protected void doStop() {
		this.unregisterMBean();
	}
	
	private ObjectName getObjectName() {
		String packageName = this.getClass().getPackage().getName();
		String name = String.format("%s:type=%s_%s",packageName,this.getClass().getSimpleName(),System.identityHashCode(this));
		try {
			ObjectName objectName = new ObjectName(name);
			return objectName;
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}
	
	private void unregisterMBean() {
		try {
			ObjectName objectName = this.getObjectName();
			if(objectName != null) {
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				try {
					if(server.isRegistered(objectName)) {
						server.unregisterMBean(objectName);					
					}
				}catch (Exception e) {
					log.error("",e);
				}
			}
		}catch (Exception e) {
			log.error("",e);
		}
	}

	private void registerMBean() {
		try {
			ObjectName objectName = this.getObjectName();
			if(objectName != null) {
				
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				try {
					if(server.isRegistered(objectName)) {
						server.unregisterMBean(objectName);					
					}
					server.registerMBean(this,objectName);
				}catch (Exception e) {
					log.error("",e);
				}
			}
		}catch (Exception e) {
			log.error("",e);
		}
	}
}
