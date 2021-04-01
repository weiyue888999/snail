package com.github.snail.logging;

public class MDC {
	
	private static boolean findDelegate = false;
	
	static {
		try {
			Class.forName("org.slf4j.MDC");
			findDelegate = true;
		} catch (ClassNotFoundException e) {
			//ignore
		}
	}

	public static void put(String key, String val) {
		if(findDelegate) {
			org.slf4j.MDC.put(key, val);			
		}
	}
	
	public static String get(String key) {
		if(findDelegate) {
			return org.slf4j.MDC.get(key);			
		}
		return "";
	}
	
	public static void remove(String key) {
		if(findDelegate) {
			org.slf4j.MDC.remove(key);			
		}
	}
}
