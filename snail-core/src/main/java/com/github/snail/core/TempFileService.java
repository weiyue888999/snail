package com.github.snail.core;

import java.io.File;

import com.github.snail.base.Lifecycle;
import com.github.snail.core.impl.Sharedable;

/**
 * @author weiguangyue
 */
public interface TempFileService extends Lifecycle, Sharedable {
	/**
	 * @description	： crate temp file
	 * @param name
	 * @return
	 */
	File create(String name);
	
	/**
	 * @description	：get temp file
	 * @param name 文件名称
	 * @return
	 */
	File get(String name);
	
	/**
	 * @description	： delete temp file
	 * @param f
	 * @return
	 */
	void delete(final File f);
	
	/**
	 * @description	：  delete temp files
	 * @param fs
	 * @return
	 */
	void delete(final File ... fs);
}
