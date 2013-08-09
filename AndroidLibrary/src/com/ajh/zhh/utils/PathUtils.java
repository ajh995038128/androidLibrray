package com.ajh.zhh.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ajh.zhh.exception.HttpException;

public class PathUtils {
	/**
	 * @author benjamin 2013/05/11
	 * */
	/**
	 * @param obj
	 *            调用者，要是使用自己定义的类，才能获取该类所在的路径
	 */
	public static String getPath(Object obj) {
		String path = obj.getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();

		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return path;
	}
}
