package com.github.snail.core;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 		：weiguangyue
 * 
 * 滑动验证码验证对象
 * 
 */
public interface Verify extends Serializable{
	
	Date getCt();

	String getImtk();

	File getSi();

	File getMi();

	int getX();

	int getY();
	
	File getImageFileById(String id);
	
	void increaseVerifyCount();
	
	long getVerifyCount();
	
}
