package com.ajh.zhh.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonObj {
	/**
	 * @author benjamin 2013/05/11
	 */
	private static Gson gson = null;
	static {
		GsonBuilder gb = new GsonBuilder();
		gb.setDateFormat("yyyy-MM-dd");
		gson = gb.create();
	}

	/**
	 * @param gsonStr
	 *            json字符串
	 * @param clazz
	 *            List对象内部的对象类型
	 * @return {@link List}
	 */
	public static <T> List<T> listFromGson(String gsonStr, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		String temp = gsonStr.replace("[", "");
		temp = temp.replace("]", "");
		temp = temp.replace("},{", "};{");
		System.out.println("temp:" + temp);
		String[] tempArray = temp.split(";");
		for (String str : tempArray) {
			list.add(gson.fromJson(str.endsWith("}") ? str : str + "}", clazz));
		}
		return list;
	}

	public static Gson getGson() {
		return gson;
	}
}
