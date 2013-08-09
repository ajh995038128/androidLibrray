package com.ajh.zhh.utils;

public class Log {
	private static boolean isDebug = true;

	/**
	 * @author benjamin 2013年5月24日
	 */
	/**
	 * 这个方法用于显示debug信息，对于一些需要在调试的时候显示但是正式发布时不希望显示的内容 可以用此方法显示
	 * @param TAG 用于标识此信息是什么地方打印出来的
	 * @param message 需要打印的信息
	 */
	public static void D(String TAG, String message) {
		if (isDebug()) {
			System.out.print("[d]---->" + TAG + ": " + message + "<----[d]\n");
		}
	}
	/**
	 * 这个方法用于显示信息，无论在调试还是正式发布都希望打印的信息可用这个方法
	 * @param TAG 用于标识此信息是什么地方打印出来的
	 * @param message 需要打印的信息
	 */
	public static void I(String TAG, String message) {
		System.out.print("[i]---->" + TAG + ": " + message + "<----[i]\n");
	}

	public static boolean isDebug() {
		return isDebug;
	}

	public static void setDebug(boolean isDebug) {
		Log.isDebug = isDebug;
	}

}
