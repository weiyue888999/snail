package com.github.snail;

import com.github.snail.common.Utils;

public class ProcessIdTest {

	public static void main(String[] args) {
		System.out.println(Utils.currentPid());
		int[] arr = Utils.currentUserJvmPids();
		for (int i : arr) {
			System.out.println(i);
		}
	}
}
