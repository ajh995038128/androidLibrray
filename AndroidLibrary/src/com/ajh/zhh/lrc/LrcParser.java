package com.ajh.zhh.lrc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcParser {
	/**
	 * @author benjamin 2013/6/16 这个类是用于解析lrc文件并获得一个{@link Lrc}对象
	 * */
	private static LrcParser instance = new LrcParser();
	private Lrc lrc;

	/**
	 * 解析完之后通过这方法获取{@link Lrc}对象
	 */
	public Lrc getLrc() {
		return lrc;
	}

	/**
	 * newInstance 用于获取{@link LrcParser}的实例
	 */
	public static LrcParser newInstance() {
		return instance;
	}

	private LrcParser() {
	};

	public void parser(String content) {
		lrc = new Lrc();
		lrc.setLrc(new ArrayList<Map<String, Object>>());
		for (String s : content.split("\n")) {
			content = removeComment(s);
			getLyricIdTags(s);
			getLyricTimeTags(s);
		}
	}

	public void parser(InputStream inputStream) throws IOException {
		String content = new String();
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len = inputStream.read(buffer)) != -1) {
			content += new String(buffer, 0, len);
		}
		parser(content);
	}

	public void parser(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		parser(in);
	}

	private String removeComment(String line) {
		Matcher m = Pattern.compile("\\[:\\]").matcher(line);
		String str = line;
		while (m.find()) {
			if (m.start() == 0)// 整行都是注释
				str = null;
			else
				str = line.substring(0, m.start());
		}
		return str;
	}

	private void getLyricIdTags(String line) {
		Matcher m = Pattern.compile("\\[((ti)|(ar)|(al)|(by)):.+\\]").matcher(
				line);
		while (m.find()) {
			String type = m.group().substring(1, 3);
			if (type.equals("ti")) {
				lrc.setTi(m.group().substring(4, m.end() - 1));
			} else if (type.equals("ar")) {
				lrc.setAr(m.group().substring(4, m.end() - 1));
			} else if (type.equals("al")) {
				lrc.setAl(m.group().substring(4, m.end() - 1));
			} else if (type.equals("by")) {
				lrc.setBy(m.group().substring(4, m.end() - 1));
			}
		}
	}

	private void getLyricTimeTags(String line) {
		Matcher m = Pattern.compile("\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]")
				.matcher(line);
		Map<String, Object> item = null;
		int begIndex = 0;
		while (m.find()) {
			item = new HashMap<String, Object>();
			item.put("time", toMillisecond(m.group()));
			begIndex = m.end();
			String content = line.substring(begIndex, line.length());
			item.put("content", content);
			lrc.getLrc().add(item);
		}
	}

	private int toMillisecond(String time) {
		int ms = 0;
		int min = 0, sec = 0, millis = 0;
		time = time.replace("[", "");
		time = time.replace("]", "");
		time = time.replace(".", ":");
		String[] tempStr = time.split(":");
		if (tempStr.length == 3) {
			if ("".equals(tempStr[0])) {
				min = 0;
			} else {
				min = Integer.parseInt(tempStr[0]);
			}
			if ("".equals(tempStr[1])) {
				sec = 0;
			} else {
				sec = Integer.parseInt(tempStr[1]);
			}
			if ("".equals(tempStr[2])) {
				millis = 0;
			} else {
				millis = Integer.parseInt(tempStr[2]);
			}

		} else if (tempStr.length == 2) {
			if ("".equals(tempStr[0])) {
				min = 0;
			} else {
				min = Integer.parseInt(tempStr[0]);
			}
			if ("".equals(tempStr[1])) {
				sec = 0;
			} else {
				sec = Integer.parseInt(tempStr[1]);
			}
		}
		ms = min * 60 * 1000 + sec * 1000 + millis;
		return ms;
	}
}
