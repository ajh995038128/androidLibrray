package com.ajh.zhh.utils;

public class TimeUtils {
	/**
	 * @author benjamin 2013/12/31<br/>
	 *         用于格式化时间转化为00:00:00格式
	 */
	private final static int HUOR = 0;
	private final static int MINUTE = 1;

	public static String secondToTime(int time) {
		return secondToTime(time, HUOR);
	}

	public static String milliSecondToTime(int time) {
		return secondToTime(time / 1000, HUOR);
	}

	private static String secondToTime(int time, int type) {
		int hour = time / (60 * 60);
		time %= 60 * 60;
		int minute = time / 60;
		time %= 60;
		int second = time;
		switch (type) {
		case MINUTE:
			return String.format("%02d:%02d", minute, second);
		case HUOR:
		default:
			return String.format("%02d:%02d:%02d", hour, minute, second);
		}
	}

	public static String seconToMinuteTime(int time) {
		return secondToTime(time, MINUTE);
	}

	public static String milliSeconToMinuteTime(int time) {
		return secondToTime(time / 1000, MINUTE);
	}
}