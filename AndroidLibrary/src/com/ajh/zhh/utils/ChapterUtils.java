package com.ajh.zhh.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterUtils {
	private final static String zore = "(零|〇)";
	private final static String one = "(一|二|三|四|五|六|七|八|九)";
	private final static String ten = "(" + one + "?" + "十" + one + "?" + ")";
	private final static String hundred = "(" + one + "百((" + ten + "+" + ")|("
			+ zore + one + "))" + ")";
	private final static String thousand = "(" + one + "千(" + hundred + "|("
			+ zore + ten + ")|(" + zore + one + "))" + ")";
	private final static String binary = "(" + one + "(" + one + "|" + zore
			+ ")*" + ")";
	private final static String number = "(" + "[0-9]*" + ")";
	private final static String start = "\\s{0,3}第";
	private final static String ends = "(回|章|节|目|幕|话|卷)";
	private final static String any = ".*";

	public static List<String> getChapter(String filePath) throws IOException {
		return getChapter(new File(filePath));
	}

	private static List<String> getChapter(File file) throws IOException {
		return getChapter(new FileInputStream(file));
	}

	private static List<String> getChapter(FileInputStream fileInputStream)
			throws IOException {
		final List<String> chapters = new ArrayList<String>();
		InputStreamReader isr = null;
		String charset = FileUtils.getTxtCharest(fileInputStream);
		System.out.println(charset);
		try {
			isr = new InputStreamReader(fileInputStream, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String regex = start + "(" + number + "|" + one + "|" + ten + "|"
				+ hundred + "|" + thousand + "|" + binary + ")" + ends + any;
		Pattern pattern = Pattern.compile(regex);
		BufferedReader br = new BufferedReader(isr);
		while (br.ready()) {
			String tempStr = br.readLine();
			Matcher matcher = pattern.matcher(tempStr);
			if (matcher.matches()) {
				chapters.add(matcher.group());
			}
		}
		return chapters;
	}

	public static void main(String[] args) throws IOException {
		String path = "C:\\Documents and Settings\\zhanghao\\桌面\\无上魔将.txt";
		List<String> chapters = getChapter(path);
		int index = 1;
		for (String chapter : chapters) {
			System.out.println("chapter[" + index++ + "]: " + chapter);
		}
	}
}
