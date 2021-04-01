package com.github.snail.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.snail.base.BaseLifecycle;
import com.github.snail.core.Verify;
import com.github.snail.core.VerifyFactory;
import com.github.snail.exceptions.VerifyException;

/**
 * @author 		：weiguangyue
 * 
 * 循环利用Verify的工厂
 * 
 * 循环利用确实能够提高效率，但这个循环利用的策略应该是全局的，很多列需要重新实现以支持重复利用
 * 
 * 1.针对临时文件，文件删除策略和这个循环利用策略要一致了！！！
 * 2.验证成功之后，verify回收策略要跟利用策略一致
 *
 */
//TODO
public class RecyclingVerifyFactory extends BaseLifecycle implements VerifyFactory{
	
	private int maxSize = 100;
	private int useIndex = 0;
	private List<Verify> list = new ArrayList<Verify>(maxSize);
	
	private VerifyFactory delegate;
	
	public RecyclingVerifyFactory(VerifyFactory delegate) {
		super();
		this.delegate = delegate;
	}
	
	private int nextIndex() {
		if(this.useIndex < this.maxSize - 1) {
			this.useIndex ++;
		}else {
			this.useIndex = 0;
		}
		return useIndex;
	}

	@Override
	public synchronized Verify create() throws VerifyException {
		if(this.list.size() <= 0) {
			Verify verify = this.delegate.create();
			this.list.add(verify);
			return verify;
		}else {
			Verify oldVerify = this.list.get(nextIndex());
			return null;
		}
	}

	@Override
	protected void doStart() {
		this.delegate.start();
		
		this.clean();
	}

	private void clean() {
		
	}

	@Override
	protected void doStop() {
		this.delegate.stop();
	}
}
