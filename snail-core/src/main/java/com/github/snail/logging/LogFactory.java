package com.github.snail.logging;

import java.lang.reflect.Constructor;

import com.github.snail.logging.slf4j.Slf4jImpl;
import com.github.snail.logging.stdout.StdOutImpl;

/**
 * @author 		：weiguangyue
 */
public final class LogFactory {

  public static final String MARKER = "SNAIL";

  private static Constructor<? extends Log> logConstructor;

  static {
    tryImplementation(new Runnable() {
		@Override
		public void run() {
			useSlf4jLogging();
		}
    });
    tryImplementation(new Runnable() {
		@Override
		public void run() {
			useStdOutLogging();
		}
    });
  }

  private LogFactory() {
    // disable construction
  }

  public static Log getLog(Class<?> aClass) {
    return getLog(aClass.getName());
  }

  public static Log getLog(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }

  public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
    setImplementation(clazz);
  }

  public static synchronized void useSlf4jLogging() {
    setImplementation(Slf4jImpl.class);
  }

  public static synchronized void useStdOutLogging() {
    setImplementation(StdOutImpl.class);
  }

  private static void tryImplementation(Runnable runnable) {
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
      }
    }
  }

  private static void setImplementation(Class<? extends Log> implClass) {
    try {
      Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
      Log log = candidate.newInstance(LogFactory.class.getName());
      if (log.isDebugEnabled()) {
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    }
  }

}
