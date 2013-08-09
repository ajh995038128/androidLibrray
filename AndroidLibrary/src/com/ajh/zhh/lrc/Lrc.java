package com.ajh.zhh.lrc;

import java.util.List;
import java.util.Map;

public class Lrc {
	/**
	 * @author benjamin 2013/06/16 这个类是代表了Lrc信息的实体类
	 */
	private String ar;
	private String ti;
	private String al;
	private String by;
	private int offset;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	private List<Map<String, Object>> lrc;

	public String getAr() {
		return ar;
	}

	public void setAr(String ar) {
		this.ar = ar;
	}

	public String getTi() {
		return ti;
	}

	public void setTi(String ti) {
		this.ti = ti;
	}

	public String getAl() {
		return al;
	}

	public void setAl(String al) {
		this.al = al;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public List<Map<String, Object>> getLrc() {
		return lrc;
	}

	public void setLrc(List<Map<String, Object>> lrc) {
		this.lrc = lrc;
	}

}
