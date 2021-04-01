package com.github.snail.common;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.snail.logging.Log;
import com.github.snail.logging.LogFactory;


public class Utils {
	
	private static int[] emptyIntArray = new int[0];
	
    private static final char UNIX_SEPARATOR = '/';

    private static final char WINDOWS_SEPARATOR = '\\';
	
	public static final char EXTENSION_SEPARATOR = '.';
	
	private static final int EOF = -1;
	
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    public static int pid = _currentPid();
    
    public static String getCurrentUser() {
    	return System.getProperty("user.name");
    }
    
    private static int _currentPid() {
    	RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    	String name = runtimeMXBean.getName();
    	String[] arr = name.split("@");
    	if(arr != null && arr.length > 1) {
    		return Integer.valueOf(arr[0]).intValue();
    	}else {
    		return 0;
    	}
    }
    
    public static int currentPid() {
    	return pid;
    }
    
    public static File getTempDilr() {
    	return new File(System.getProperty("java.io.tmpdir"));
    }
    
    public static int[] currentUserJvmPids() {
    	String jvmTempDirStr = "hsperfdata_" + getCurrentUser();
    	File jvmTempDir = new File(getTempDilr(),jvmTempDirStr);
    	File[] fs = jvmTempDir.listFiles();
    	if(fs != null && fs.length > 0) {
    		int[] ret = new int[fs.length];
    		int i = -1;
    		for(File f : fs) {
    			i++;
    			String name = f.getName();
    			ret[i] = Integer.parseInt(name);
    		}
    		return ret;
    	}
    	return emptyIntArray;
    }
    
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }
    private static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    private static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
    private static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }
    private static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }
    

	public static class ExecutorUtil {
		private static final Log logger = LogFactory.getLog(Utils.class);
		private static final ThreadPoolExecutor shutdownExecutor = new ThreadPoolExecutor(0, 1, 0L,TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100),new NamedThreadFactory("Close-ExecutorService-Timer", true));

		public static boolean isTerminated(Executor executor) {
			if (executor instanceof ExecutorService) {
				if (((ExecutorService) executor).isTerminated()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Use the shutdown pattern from:
		 * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
		 * 
		 * @param executor
		 *            the Executor to shutdown
		 * @param timeout
		 *            the timeout in milliseconds before termination
		 */
		public static void gracefulShutdown(Executor executor, int timeout) {
			if (!(executor instanceof ExecutorService) || isTerminated(executor)) {
				return;
			}
			final ExecutorService es = (ExecutorService) executor;
			try {
				// Disable new tasks from being submitted
				es.shutdown();
			} catch (SecurityException ex2) {
				return;
			} catch (NullPointerException ex2) {
				return;
			}
			try {
				// Wait a while for existing tasks to terminate
				if (!es.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
					es.shutdownNow();
				}
			} catch (InterruptedException ex) {
				es.shutdownNow();
				Thread.currentThread().interrupt();
			}
			if (!isTerminated(es)) {
				newThreadToCloseExecutor(es);
			}
		}

		public static void shutdownNow(Executor executor, final int timeout) {
			if (!(executor instanceof ExecutorService) || isTerminated(executor)) {
				return;
			}
			final ExecutorService es = (ExecutorService) executor;
			try {
				es.shutdownNow();
			} catch (SecurityException ex2) {
				return;
			} catch (NullPointerException ex2) {
				return;
			}
			try {
				es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			if (!isTerminated(es)) {
				newThreadToCloseExecutor(es);
			}
		}

		private static void newThreadToCloseExecutor(final ExecutorService es) {
			if (!isTerminated(es)) {
				shutdownExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							for (int i = 0; i < 1000; i++) {
								es.shutdownNow();
								if (es.awaitTermination(10, TimeUnit.MILLISECONDS)) {
									break;
								}
							}
						} catch (InterruptedException ex) {
							Thread.currentThread().interrupt();
						} catch (Throwable e) {
							logger.warn(e.getMessage(), e);
						}
					}
				});
			}
		}
	}
	
	/**
	 * InternalThreadFactory.
	 */
	public static class NamedThreadFactory implements ThreadFactory {

	    protected static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	    protected final AtomicInteger mThreadNum = new AtomicInteger(1);

	    protected final String mPrefix;

	    protected final boolean mDaemon;

	    protected final ThreadGroup mGroup;

	    public NamedThreadFactory() {
	        this("pool-" + POOL_SEQ.getAndIncrement(), false);
	    }

	    public NamedThreadFactory(String prefix) {
	        this(prefix, false);
	    }

	    public NamedThreadFactory(String prefix, boolean daemon) {
	        mPrefix = prefix + "-thread-";
	        mDaemon = daemon;
	        SecurityManager s = System.getSecurityManager();
	        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	    }

	    @Override
	    public Thread newThread(Runnable runnable) {
	        String name = mPrefix + mThreadNum.getAndIncrement();
	        Thread ret = new Thread(mGroup, runnable, name, 0);
	        ret.setDaemon(mDaemon);
	        return ret;
	    }

	    public ThreadGroup getThreadGroup() {
	        return mGroup;
	    }
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static boolean isEmpty(String str) {
		if(str == null || "".equals(str)) {
			return true;
		}
		String trimStr = str.trim();
		if(trimStr.length() <= 0) {
			return true;
		}
		return false;
	}
	
	public static String[] split(String toSplit, String delimiter) {
		if (!hasLength(toSplit) || !hasLength(delimiter)) {
			return null;
		}
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0) {
			return null;
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[] {beforeDelimiter, afterDelimiter};
	}
	
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}
	
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}
	
	public static int parseInt(String val,String paramName) {
		try {
			int num = Integer.parseInt(val);
			if(num <= 0) {
				throw new IllegalArgumentException("param ["+ paramName +"] must be integer");
			}
			return num;
		}catch (Exception e) {
			throw new IllegalArgumentException("param ["+ paramName +"] must be integer greater than 0");
		}
	}
	
	public static int parsePositiveInt(String val,String paramName) {
		try {
			int num = Integer.parseInt(val);
			if(num <= 0) {
				throw new IllegalArgumentException("param ["+ paramName +"] must be integer greater than 0");
			}
			return num;
		}catch (Exception e) {
			throw new IllegalArgumentException("param ["+ paramName +"] must be integer greater than 0");
		}
	}

	public static long parsePositiveLong(String val,String paramName) {
		try {
			long num = Long.parseLong(val);
			if(num <= 0) {
				throw new IllegalArgumentException("param ["+ paramName +"] must be integer greater than 0");
			}
			return num;
		}catch (Exception e) {
			throw new IllegalArgumentException("param ["+ paramName +"] must be integer greater than 0");
		}
	}

	public static String join(List<String> list, String token) {
		StringBuilder builder = new StringBuilder();
		for(String str : list) {
			builder.append(str).append(token);
		}
		return builder.toString();
	}

	public static byte[] cloneByteArry(byte[] srcByteArray) {
		int length = srcByteArray.length;
		byte[] clone = new byte[length];
		System.arraycopy(srcByteArray, 0, clone, 0, length);
		return clone;
	}
}
